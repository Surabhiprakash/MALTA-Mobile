package com.malta_mqf.malta_mobile;

import android.content.Context;
import android.widget.Toast;

import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OrderValidationHelper {

    public static boolean validateItems(
            Context context,
            List<Map.Entry<String, String>> selectedproduct,
            ItemsByAgencyDB db,
            String customerCode
    ) {

        System.out.println("========== VALIDATION START ==========");
        System.out.println("CustomerCode: " + customerCode);
        System.out.println("Selected Items Count: " + selectedproduct.size());

        int validCount = 0;
        int invalidCount = 0;

        Set<String> validAgencySet = new HashSet<>();

        for (Map.Entry<String, String> entry : selectedproduct) {

            String itemName = entry.getKey();
            String qty = entry.getValue();

            System.out.println("\n--- ITEM START ---");
            System.out.println("ItemName: " + itemName);
            System.out.println("Qty: " + qty);

            if (qty == null || qty.trim().isEmpty() || "0".equals(qty.trim())) {
                System.out.println("❌ Skipped (invalid qty)");
                continue;
            }

            // 🔥 STEP 1: Get itemCode
            String itemCode = db.getItemCodeByName(itemName);
            System.out.println("ItemCode: " + itemCode);

            if (itemCode == null || itemCode.trim().isEmpty()) {
                System.out.println("❌ ItemCode NOT FOUND → Mark invalid");
                invalidCount++;
                continue;
            }

            // 🔥 STEP 2: Get agency (IMPORTANT FIX)
            String agency = db.checkforproductsagency(itemName);
            System.out.println("Agency from DB: " + agency);

            boolean isValid = false;

            // 🔥 STEP 3: Validate only if agency exists
            if (agency != null && !agency.trim().isEmpty()) {

                isValid = db.isItemValidForCustomer(
                        customerCode.trim(),
                        agency.trim(),
                        itemCode.trim()
                );

                System.out.println("Validation Result: " + isValid);

            } else {
                System.out.println("❌ Agency NULL → Mark invalid");
            }

            if (isValid) {
                validCount++;
                validAgencySet.add(agency.trim());
                System.out.println("✅ Counted as VALID");
            } else {
                invalidCount++;
                System.out.println("❌ Counted as INVALID");
            }

            System.out.println("--- ITEM END ---");
        }

        System.out.println("\n========== FINAL COUNTS ==========");
        System.out.println("validCount: " + validCount);
        System.out.println("invalidCount: " + invalidCount);
        System.out.println("validAgencySet: " + validAgencySet);

        // 🔥 DECISION LOGS
        if (validCount > 0 && invalidCount > 0) {
            System.out.println("❌ MIXED CASE → BLOCK");
            Toast.makeText(context,
                    "Mix of direct billing and non-direct billing items.",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (validCount == 0) {
            System.out.println("✅ ALL INVALID → ALLOW");
            return true;
        }

        if (validAgencySet.size() == 1) {
            System.out.println("✅ SINGLE AGENCY → ALLOW");
            return true;
        }

        System.out.println("❌ MULTIPLE AGENCIES → BLOCK");

        Toast.makeText(context,
                "Multiple agencies found for direct billing items.",
                Toast.LENGTH_LONG).show();

        return false;
    }
}
