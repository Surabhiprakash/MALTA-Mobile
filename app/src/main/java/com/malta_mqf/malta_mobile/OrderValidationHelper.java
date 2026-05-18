package com.malta_mqf.malta_mobile;

import android.content.Context;
import android.widget.Toast;

import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OrderValidationHelper {

    public enum BillingType {
        INDIVIDUAL_BILLING,
        MALTA_BILLING,
        BLOCKED
    }

    private static BillingType billingType;
    private static String billingAgency;

    public static BillingType getBillingType() {
        return billingType;
    }

    public static String getBillingAgency() {
        return billingAgency;
    }

    public static boolean validateItems(
            Context context,
            List<Map.Entry<String, String>> selectedproduct,
            ItemsByAgencyDB db,
            String customerCode
    ) {
        System.out.println("Selected customer before validation: " + customerCode);

        billingType = null;
        billingAgency = null;

        int validCount = 0;
        int invalidCount = 0;

        Set<String> validAgencySet = new HashSet<>();

        // 🔥 SAFE CUSTOMER
        String safeCustomer = (customerCode != null) ? customerCode.trim() : "";

        for (Map.Entry<String, String> entry : selectedproduct) {

            String itemName = entry.getKey();
            String qty = entry.getValue();

            if (qty == null || qty.trim().isEmpty() || "0".equals(qty.trim())) {
                continue;
            }

            String itemCode = db.getItemCodeByName(itemName);

            if (itemCode == null || itemCode.trim().isEmpty()) {
                System.out.println("❌ ItemCode NULL for: " + itemName);
                invalidCount++;
                continue;
            }

            String safeItemCode = itemCode.trim();

            // ✅ FIXED: use itemCode
            String agency = db.checkforproductsagency(itemName);

            String safeAgency = (agency != null) ? agency.trim() : "";

            System.out.println("Item: " + itemName +
                    " | Code: " + safeItemCode +
                    " | Agency: " + safeAgency +
                    " | Customer: " + safeCustomer);

            boolean isValid = false;

            if (!safeAgency.isEmpty() && !safeCustomer.isEmpty()) {

                isValid = db.isItemValidForCustomer(
                        safeCustomer,
                        safeAgency,
                        safeItemCode
                );

                System.out.println("Validation Result: " + isValid);

            } else {
                System.out.println("❌ Skipping validation (agency/customer empty)");
            }

            if (isValid) {
                validCount++;
                validAgencySet.add(safeAgency);
            } else {
                invalidCount++;
            }
        }

        System.out.println("validCount: " + validCount);
        System.out.println("invalidCount: " + invalidCount);
        System.out.println("validAgencySet: " + validAgencySet);

        // =========================
        // 🔥 FINAL DECISION
        // =========================

        // ❌ MIXED → BLOCK
        if (validCount > 0 && invalidCount > 0) {

            billingType = BillingType.BLOCKED;

            Toast.makeText(context,
                    "Mix of direct billing and non-direct billing items.",
                    Toast.LENGTH_LONG).show();

            return false;
        }

        // ✅ ALL INVALID → MALTA
        if (validCount == 0) {

            billingType = BillingType.MALTA_BILLING;
            billingAgency = null;

            System.out.println("✔ MALTA BILLING");
            System.out.println("pointing here ");
            return true;
        }

        // ✅ ALL VALID → SAME AGENCY
        if (validAgencySet.size() == 1) {

            billingType = BillingType.INDIVIDUAL_BILLING;
            billingAgency = validAgencySet.iterator().next();

            System.out.println("✔ INDIVIDUAL BILLING → " + billingAgency);

            return true;
        }

        // ❌ MULTIPLE AGENCIES
        billingType = BillingType.BLOCKED;

        Toast.makeText(context,
                "Multiple agencies found for direct billing items.",
                Toast.LENGTH_LONG).show();

        return false;
    }

    public static void determineBillingTypeOnly(
            List<Map.Entry<String, String>> selectedproduct,
            ItemsByAgencyDB db,
            String customerCode
    ) {

        billingType = null;
        billingAgency = null;

        int validCount = 0;
        Set<String> validAgencySet = new HashSet<>();

        for (Map.Entry<String, String> entry : selectedproduct) {

            String itemName = entry.getKey();
            String qty = entry.getValue();

            if (qty == null || qty.trim().isEmpty() || "0".equals(qty.trim())) {
                continue;
            }

            String itemCode = db.getItemCodeByName(itemName);

            if (itemCode == null || itemCode.trim().isEmpty()) {
                continue;
            }

            String agency = db.checkforproductsagency(itemName);

            if (agency != null && !agency.trim().isEmpty()) {

                boolean isValid = db.isItemValidForCustomer(
                        customerCode == null ? "" : customerCode.trim(),
                        agency.trim(),
                        itemCode.trim()
                );

                if (isValid) {
                    validCount++;
                    validAgencySet.add(agency.trim());
                }
            }
        }

        // =========================
        // 🔥 BILLING TYPE ONLY (NO BLOCKING)
        // =========================

        if (validCount == 0) {
            billingType = BillingType.MALTA_BILLING;
            billingAgency = null;
        } else if (validAgencySet.size() == 1) {
            billingType = BillingType.INDIVIDUAL_BILLING;
            billingAgency = validAgencySet.iterator().next();
        } else {
            // Multiple agencies but configured → treat as MALTA (as per your rule)
            billingType = BillingType.MALTA_BILLING;
            billingAgency = null;
        }

        System.out.println("RETURN FLOW → BillingType: " + billingType);
        System.out.println("RETURN FLOW → BillingAgency: " + billingAgency);
    }
}