package com.malta_mqf.malta_mobile;

import android.content.Context;
import android.widget.Toast;

import com.malta_mqf.malta_mobile.DataBase.ItemsByAgencyDB;
import com.malta_mqf.malta_mobile.Utilities.CustomerLogger;

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

        CustomerLogger.i("OrderValidation",
                "=========== VALIDATION START ===========");

        CustomerLogger.i("OrderValidation",
                "Incoming Customer=[" + customerCode + "]");

        CustomerLogger.i("OrderValidation",
                "CustomerLength=" +
                        (customerCode == null ? 0 : customerCode.length()));

        billingType = null;
        billingAgency = null;

        int validCount = 0;
        int invalidCount = 0;

        Set<String> validAgencySet = new HashSet<>();

        String safeCustomer =
                (customerCode != null)
                        ? customerCode.trim()
                        : "";

        CustomerLogger.i("OrderValidation",
                "SafeCustomer=[" + safeCustomer + "]");

        CustomerLogger.i("OrderValidation",
                "SafeCustomerLength=" + safeCustomer.length());

        int itemNo = 1;

        for (Map.Entry<String, String> entry : selectedproduct) {

            CustomerLogger.i("OrderValidation",
                    "------------- ITEM " + itemNo + " -------------");

            String itemName = entry.getKey();
            String qty = entry.getValue();

            CustomerLogger.i("OrderValidation",
                    "RawItemName=[" + itemName + "]");

            CustomerLogger.i("OrderValidation",
                    "ItemNameLength=" +
                            (itemName == null ? 0 : itemName.length()));

            CustomerLogger.i("OrderValidation",
                    "Qty=[" + qty + "]");


            if (qty == null ||
                    qty.trim().isEmpty() ||
                    "0".equals(qty.trim())) {

                CustomerLogger.i("OrderValidation",
                        "SKIPPED -> qty empty/0");

                itemNo++;
                continue;
            }


            String safeItemName =
                    itemName == null
                            ? ""
                            : itemName.trim();

            CustomerLogger.i("OrderValidation",
                    "SafeItemName=[" + safeItemName + "]");


            CustomerLogger.i("OrderValidation",
                    "Calling getItemCodeByName");

            String itemCode =
                    db.getItemCodeByName(
                            safeItemName
                    );

            CustomerLogger.i("OrderValidation",
                    "Returned ItemCode=[" + itemCode + "]");


            if (itemCode == null ||
                    itemCode.trim().isEmpty()) {

                CustomerLogger.i("OrderValidation",
                        "❌ ITEM CODE NULL");

                CustomerLogger.i("OrderValidation",
                        "FAILED ITEM=[" +
                                safeItemName + "]");

                invalidCount++;
                itemNo++;
                continue;
            }

            String safeItemCode =
                    itemCode.trim();

            CustomerLogger.i("OrderValidation",
                    "SafeItemCode=[" +
                            safeItemCode + "]");

            CustomerLogger.i("OrderValidation",
                    "ItemCodeLength=" +
                            safeItemCode.length());


            CustomerLogger.i("OrderValidation",
                    "Calling checkforproductsagency");

            String agency =
                    db.checkforproductsagency(
                            safeItemName
                    );

            CustomerLogger.i("OrderValidation",
                    "ReturnedAgency=[" +
                            agency + "]");


            String safeAgency =
                    agency != null
                            ? agency.trim()
                            : "";

            CustomerLogger.i("OrderValidation",
                    "SafeAgency=[" +
                            safeAgency + "]");

            CustomerLogger.i("OrderValidation",
                    "AgencyLength=" +
                            safeAgency.length());

            boolean isValid = false;

            if (!safeAgency.isEmpty()
                    && !safeCustomer.isEmpty()) {

                CustomerLogger.i("OrderValidation",
                        "Calling isItemValidForCustomer");

                CustomerLogger.i("OrderValidation",
                        "customer=[" +
                                safeCustomer + "]");

                CustomerLogger.i("OrderValidation",
                        "agency=[" +
                                safeAgency + "]");

                CustomerLogger.i("OrderValidation",
                        "itemCode=[" +
                                safeItemCode + "]");

                isValid =
                        db.isItemValidForCustomer(
                                safeCustomer,
                                safeAgency,
                                safeItemCode
                        );

                CustomerLogger.i("OrderValidation",
                        "ValidationResult="
                                + isValid);

            } else {

                CustomerLogger.i("OrderValidation",
                        "❌ Validation Skipped");

                if (safeAgency.isEmpty()) {

                    CustomerLogger.i("OrderValidation",
                            "Reason=Agency Empty");
                }

                if (safeCustomer.isEmpty()) {

                    CustomerLogger.i("OrderValidation",
                            "Reason=Customer Empty");
                }
            }


            if (isValid) {

                validCount++;

                validAgencySet.add(
                        safeAgency
                );

                CustomerLogger.i("OrderValidation",
                        "✔ VALID ITEM");

            } else {

                invalidCount++;

                CustomerLogger.i("OrderValidation",
                        "❌ INVALID ITEM");
            }

            CustomerLogger.i("OrderValidation",
                    "CurrentValidCount="
                            + validCount);

            CustomerLogger.i("OrderValidation",
                    "CurrentInvalidCount="
                            + invalidCount);

            CustomerLogger.i("OrderValidation",
                    "CurrentAgencySet="
                            + validAgencySet);

            itemNo++;
        }


        CustomerLogger.i("OrderValidation",
                "=========== FINAL COUNTS ===========");

        CustomerLogger.i("OrderValidation",
                "validCount=" + validCount);

        CustomerLogger.i("OrderValidation",
                "invalidCount=" + invalidCount);

        CustomerLogger.i("OrderValidation",
                "validAgencySet="
                        + validAgencySet);


        if (validCount == 0 &&
                invalidCount > 0) {

            CustomerLogger.i("OrderValidation",
                    "WARNING: ALL ITEMS FAILED VALIDATION");
        }


        if (validCount > 0 &&
                invalidCount > 0) {

            CustomerLogger.i("OrderValidation",
                    "FINAL RESULT -> BLOCKED(MIXED)");

            billingType =
                    BillingType.BLOCKED;

            Toast.makeText(
                    context,
                    "Mix of direct billing and non-direct billing items.",
                    Toast.LENGTH_LONG
            ).show();

            return false;
        }


        if (validCount == 0) {

            CustomerLogger.i("OrderValidation",
                    "FINAL RESULT -> MALTA");

            CustomerLogger.i("OrderValidation",
                    "Reason=validCount=0");

            billingType =
                    BillingType.MALTA_BILLING;

            billingAgency = null;

            return true;
        }


        if (validAgencySet.size() == 1) {

            billingType =
                    BillingType.INDIVIDUAL_BILLING;

            billingAgency =
                    validAgencySet
                            .iterator()
                            .next();

            CustomerLogger.i("OrderValidation",
                    "FINAL RESULT -> INDIVIDUAL");

            CustomerLogger.i("OrderValidation",
                    "Agency=[" +
                            billingAgency +
                            "]");

            return true;
        }


        CustomerLogger.i("OrderValidation",
                "FINAL RESULT -> BLOCKED MULTIPLE AGENCY");


        billingType =
                BillingType.BLOCKED;


        Toast.makeText(
                context,
                "Multiple agencies found for direct billing items.",
                Toast.LENGTH_LONG
        ).show();

        return false;
    }
    public static void determineBillingTypeOnly(
            List<Map.Entry<String, String>> selectedproduct,
            ItemsByAgencyDB db,
            String customerCode
    ) {

        CustomerLogger.i("OrderValidation",
                "=========== determineBillingTypeOnly START ===========");

        billingType = null;
        billingAgency = null;

        int validCount = 0;
        Set<String> validAgencySet = new HashSet<>();

        String safeCustomer =
                customerCode == null
                        ? ""
                        : customerCode.trim();

        CustomerLogger.i("OrderValidation",
                "Customer=[" + safeCustomer + "]");

        CustomerLogger.i("OrderValidation",
                "CustomerLength=" + safeCustomer.length());

        int itemNo = 1;

        for (Map.Entry<String, String> entry : selectedproduct) {

            CustomerLogger.i("OrderValidation",
                    "---------- ITEM " + itemNo + " ----------");

            String itemName = entry.getKey();
            String qty = entry.getValue();

            CustomerLogger.i("OrderValidation",
                    "RawItemName=[" + itemName + "]");

            CustomerLogger.i("OrderValidation",
                    "ItemNameLength="
                            + (itemName == null ? 0 : itemName.length()));

            CustomerLogger.i("OrderValidation",
                    "Qty=[" + qty + "]");


            if (qty == null ||
                    qty.trim().isEmpty() ||
                    "0".equals(qty.trim())) {

                CustomerLogger.i("OrderValidation",
                        "SKIPPED qty empty/0");

                itemNo++;
                continue;
            }

            String safeItemName =
                    itemName == null
                            ? ""
                            : itemName.trim();

            CustomerLogger.i("OrderValidation",
                    "SafeItemName=[" + safeItemName + "]");


            // ITEM CODE

            CustomerLogger.i("OrderValidation",
                    "Calling getItemCodeByName");

            String itemCode =
                    db.getItemCodeByName(
                            safeItemName
                    );

            CustomerLogger.i("OrderValidation",
                    "ReturnedItemCode=[" + itemCode + "]");

            if (itemCode == null ||
                    itemCode.trim().isEmpty()) {

                CustomerLogger.i("OrderValidation",
                        "❌ ITEM CODE NULL");

                CustomerLogger.i("OrderValidation",
                        "FAILED ITEM=["
                                + safeItemName
                                + "]");

                itemNo++;
                continue;
            }

            String safeItemCode =
                    itemCode.trim();

            CustomerLogger.i("OrderValidation",
                    "SafeItemCode=[" +
                            safeItemCode + "]");

            CustomerLogger.i("OrderValidation",
                    "ItemCodeLength="
                            + safeItemCode.length());


            // AGENCY

            CustomerLogger.i("OrderValidation",
                    "Calling checkforproductsagency");

            String agency =
                    db.checkforproductsagency(
                            safeItemName
                    );

            CustomerLogger.i("OrderValidation",
                    "ReturnedAgency=["
                            + agency + "]");

            String safeAgency =
                    agency == null
                            ? ""
                            : agency.trim();

            CustomerLogger.i("OrderValidation",
                    "SafeAgency=["
                            + safeAgency + "]");

            CustomerLogger.i("OrderValidation",
                    "AgencyLength="
                            + safeAgency.length());


            if (!safeAgency.isEmpty()) {

                CustomerLogger.i("OrderValidation",
                        "Calling isItemValidForCustomer");

                CustomerLogger.i("OrderValidation",
                        "customer=["
                                + safeCustomer
                                + "]");

                CustomerLogger.i("OrderValidation",
                        "agency=["
                                + safeAgency
                                + "]");

                CustomerLogger.i("OrderValidation",
                        "itemCode=["
                                + safeItemCode
                                + "]");

                boolean isValid =
                        db.isItemValidForCustomer(
                                safeCustomer,
                                safeAgency,
                                safeItemCode
                        );

                CustomerLogger.i("OrderValidation",
                        "ValidationResult="
                                + isValid);

                if (isValid) {

                    validCount++;

                    validAgencySet.add(
                            safeAgency
                    );

                    CustomerLogger.i("OrderValidation",
                            "✔ VALID ITEM");

                } else {

                    CustomerLogger.i("OrderValidation",
                            "❌ INVALID ITEM");
                }

            } else {

                CustomerLogger.i("OrderValidation",
                        "Agency empty -> skipped");
            }


            CustomerLogger.i("OrderValidation",
                    "CurrentValidCount="
                            + validCount);

            CustomerLogger.i("OrderValidation",
                    "CurrentAgencySet="
                            + validAgencySet);

            itemNo++;
        }


        CustomerLogger.i("OrderValidation",
                "=========== FINAL RESULT ===========");

        CustomerLogger.i("OrderValidation",
                "validCount=" + validCount);

        CustomerLogger.i("OrderValidation",
                "validAgencySet=" + validAgencySet);


        if (validCount == 0) {

            billingType =
                    BillingType.MALTA_BILLING;

            billingAgency = null;

            CustomerLogger.i("OrderValidation",
                    "FINAL -> MALTA");

            CustomerLogger.i("OrderValidation",
                    "Reason=validCount=0");

        }
        else if (validAgencySet.size() == 1) {

            billingType =
                    BillingType.INDIVIDUAL_BILLING;

            billingAgency =
                    validAgencySet
                            .iterator()
                            .next();

            CustomerLogger.i("OrderValidation",
                    "FINAL -> INDIVIDUAL");

            CustomerLogger.i("OrderValidation",
                    "Agency=["
                            + billingAgency
                            + "]");

        }
        else {

            billingType =
                    BillingType.MALTA_BILLING;

            billingAgency = null;

            CustomerLogger.i("OrderValidation",
                    "FINAL -> MALTA");

            CustomerLogger.i("OrderValidation",
                    "Reason=MultipleAgencies");
        }

        CustomerLogger.i("OrderValidation",
                "ReturnBillingType="
                        + billingType);

        CustomerLogger.i("OrderValidation",
                "ReturnBillingAgency="
                        + billingAgency);

        CustomerLogger.i("OrderValidation",
                "=========== END ===========");
    }
}