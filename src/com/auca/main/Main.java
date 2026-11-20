package com.auca.main;

import com.auca.Individualtaxpayer.IndividualTaxpayer;
import com.auca.abstracts.RegisteredTaxpayer;
import com.auca.corporatetaxpayer.CorporateTaxpayer;
import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.InvalidStatusTransitionException;
import com.auca.exceptions.InvalidTaxCalculationException;
import com.auca.exceptions.InvalidTaxPeriodException;
import com.auca.exceptions.PrerequisiteMissingException;
import com.auca.exceptions.RecordNotFoundException;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Validatable;
import com.auca.penalty.Penalty;
import com.auca.revenueofficer.RevenueOfficer;
import com.auca.taxassessment.TaxAssessment;
import com.auca.taxclearance.TaxClearance;
import com.auca.taxdeclaration.TaxDeclaration;
import com.auca.taxobligation.TaxObligation;
import com.auca.taxpayment.TaxPayment;
import com.auca.taxrefund.TaxRefund;
import com.auca.taxtype.TaxType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Main {


    private static final Scanner sc = new Scanner(System.in);


    private static IndividualTaxpayer[] individuals = new IndividualTaxpayer[100];
    private static int individualCount = 0;

    private static CorporateTaxpayer[] corporates = new CorporateTaxpayer[100];
    private static int corporateCount = 0;

    private static RevenueOfficer[] officers = new RevenueOfficer[50];
    private static int officerCount = 0;

    private static TaxType[] taxTypes = new TaxType[30];
    private static int taxTypeCount = 0;

    private static TaxObligation[] obligations = new TaxObligation[200];
    private static int obligationCount = 0;

    private static TaxDeclaration[] declarations = new TaxDeclaration[200];
    private static int declarationCount = 0;

    private static TaxAssessment[] assessments = new TaxAssessment[200];
    private static int assessmentCount = 0;

    private static TaxPayment[] payments = new TaxPayment[300];
    private static int paymentCount = 0;

    private static Penalty[] penalties = new Penalty[200];
    private static int penaltyCount = 0;

    private static TaxRefund[] refunds = new TaxRefund[100];
    private static int refundCount = 0;

    private static TaxClearance[] clearances = new TaxClearance[100];
    private static int clearanceCount = 0;

    private static final BigDecimal MAX_MONEY = new BigDecimal("999999999999.99");


    private static final String[] PARTY_LABELS = {
            "Party ID", "First name", "Last name", "Email", "Phone", "Date of birth"};
    private static final String[] PARTY_TYPES = {
            "TEXT:4:30", "TEXT:2:60", "TEXT:2:60", "EMAIL", "PHONE", "DATE"};
    private static final String[] PARTY_HINTS = {
            "String, unique, 4-30", "String, 2-60", "String, 2-60",
            "String, valid email, max 120", "String, 10-15 digits, optional +",
            "LocalDate, YYYY-MM-DD, not future"};

    private static final String[] TAXPAYER_LABELS = {
            "TIN", "Registration date", "Status", "District", "Sector", "Tax office"};
    private static final String[] TAXPAYER_TYPES = {
            "TIN", "DATE", "STATUS", "TEXT:2:60", "TEXT:2:60", "TEXT:2:60"};
    private static final String[] TAXPAYER_HINTS = {
            "String, 9 digits, unique", "LocalDate, YYYY-MM-DD, not future",
            "String, ACTIVE / INACTIVE / SUSPENDED", "String, 2-60", "String, 2-60", "String, 2-60"};


    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            printMenu();
            System.out.print("Select option: ");
            String line = readLine().trim();

            int option;
            try {
                option = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Invalid option. Enter a number from the menu.");
                continue;
            }

            try {
                switch (option) {
                    case 0:
                        running = false;
                        System.out.println("Goodbye.");
                        break;
                    case 1:
                        registerIndividual();
                        break;
                    case 2:
                        registerCorporate();
                        break;
                    case 3:
                        registerOfficer();
                        break;
                    case 4:
                        registerTaxType();
                        break;
                    case 5:
                        createObligation();
                        break;
                    case 6:
                        fileDeclaration();
                        break;
                    case 7:
                        assessDeclaration();
                        break;
                    case 8:
                        confirmAssessment();
                        break;
                    default:
                        if (option >= 9 && option <= 21) {
                            System.out.println("Option " + option + " will be added in the next part.");
                        } else {
                            System.out.println("Unknown option. Choose a number from the menu.");
                        }
                }
            } catch (RuntimeException e) {

                System.out.println("Unexpected problem: " + e.getMessage() + ". Returning to the menu.");
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("================ TAX ADMINISTRATION MANAGEMENT ================");
        System.out.println(" 1 Register Individual Taxpayer      12 Record Tax Payment");
        System.out.println(" 2 Register Corporate Taxpayer       13 Calculate / Record Penalty");
        System.out.println(" 3 Register Revenue Officer          14 Request / Approve Refund");
        System.out.println(" 4 Register Tax Type                 15 Request / Issue Tax Clearance");
        System.out.println(" 5 Create Tax Obligation             16 Search Record by ID / TIN");
        System.out.println(" 6 File Tax Declaration              17 Update Eligible Record");
        System.out.println(" 7 Assess Declaration                18 View Taxpayer Statement");
        System.out.println(" 8 Confirm Assessment                19 View Outstanding Balances");
        System.out.println(" 9 View All Records                  20 View Tax Reports");
        System.out.println("10 View Declaration History          21 Demonstrate Polymorphism");
        System.out.println("11 View Assessment History            0 Exit");
    }


    private static void registerIndividual() {
        if (individualCount >= individuals.length) {
            System.out.println("Individual taxpayer storage is full (" + individuals.length
                    + "). Cannot register more.");
            return;
        }

        String[] labels = concat(concat(PARTY_LABELS, TAXPAYER_LABELS), new String[]{
                "National ID", "Occupation", "Business name", "Business start date",
                "Annual turnover", "Resident"});
        String[] types = concat(concat(PARTY_TYPES, TAXPAYER_TYPES), new String[]{
                "NATID", "TEXT:2:60", "TEXT:2:120", "DATE", "MONEY", "YESNO"});
        String[] hints = concat(concat(PARTY_HINTS, TAXPAYER_HINTS), new String[]{
                "String, 16 digits, unique", "String, 2-60", "String, 2-120",
                "LocalDate, YYYY-MM-DD, not future", "BigDecimal, >= 0, 2 decimals",
                "boolean, YES / NO"});

        boolean saved = runForm("INDIVIDUAL", "REGISTER INDIVIDUAL TAXPAYER", labels, types, hints,
                new int[]{6, 12, 18},
                new String[]{"PERSONAL DETAILS", "TAX REGISTRATION", "BUSINESS DETAILS"});

        if (saved) {
            System.out.println(individuals[individualCount - 1].getSummary());
        }
    }

    private static void registerCorporate() {
        if (corporateCount >= corporates.length) {
            System.out.println("Corporate taxpayer storage is full (" + corporates.length
                    + "). Cannot register more.");
            return;
        }

        String[] labels = concat(concat(PARTY_LABELS, TAXPAYER_LABELS), new String[]{
                "Company reg. no", "Legal name", "Incorporation date", "Business sector",
                "Authorized representative", "Annual turnover"});
        String[] types = concat(concat(PARTY_TYPES, TAXPAYER_TYPES), new String[]{
                "COMPNO", "TEXT:3:120", "DATE", "TEXT:2:60", "TEXT:2:60", "MONEY"});
        String[] hints = concat(concat(PARTY_HINTS, TAXPAYER_HINTS), new String[]{
                "String, 5-30 A-Z 0-9 -, unique", "String, 3-120",
                "LocalDate, YYYY-MM-DD, not future", "String, 2-60", "String, 2-60",
                "BigDecimal, >= 0, 2 decimals"});

        boolean saved = runForm("CORPORATE", "REGISTER CORPORATE TAXPAYER", labels, types, hints,
                new int[]{6, 12, 18},
                new String[]{"CONTACT PERSON DETAILS", "TAX REGISTRATION", "COMPANY DETAILS"});

        if (saved) {
            System.out.println(corporates[corporateCount - 1].getSummary());
        }
    }

    private static void registerOfficer() {
        if (officerCount >= officers.length) {
            System.out.println("Revenue officer storage is full (" + officers.length
                    + "). Cannot register more.");
            return;
        }

        String[] labels = concat(PARTY_LABELS, new String[]{
                "Staff number", "Department", "Role", "Hire date", "Approval limit", "Active"});
        String[] types = concat(PARTY_TYPES, new String[]{
                "TEXT:3:30", "TEXT:2:60", "TEXT:2:60", "DATE", "MONEY", "YESNO"});
        String[] hints = concat(PARTY_HINTS, new String[]{
                "String, unique, 3-30", "String, 2-60", "String, 2-60",
                "LocalDate, YYYY-MM-DD, not future", "BigDecimal, >= 0, 2 decimals",
                "boolean, YES / NO"});

        boolean saved = runForm("OFFICER", "REGISTER REVENUE OFFICER", labels, types, hints,
                new int[]{6, 9, 12},
                new String[]{"PERSONAL DETAILS", "STAFF DETAILS", "AUTHORITY AND STATUS"});

        if (saved) {
            System.out.println(officers[officerCount - 1].getSummary());
        }
    }

    private static void registerTaxType() {
        if (taxTypeCount >= taxTypes.length) {
            System.out.println("Tax type storage is full (" + taxTypes.length
                    + "). Cannot register more.");
            return;
        }

        String[] labels = {"Tax type ID", "Tax code", "Tax name", "Rate (%)",
                "Filing frequency", "Active"};
        String[] types = {"TEXT:2:30", "TEXT:2:20", "TEXT:2:60", "RATE", "FREQ", "YESNO"};
        String[] hints = {"String, unique, 2-30", "String, unique, 2-20", "String, 2-60",
                "BigDecimal, 0 to 100, 2 decimals", "String, MONTHLY / QUARTERLY / ANNUAL",
                "boolean, YES / NO"};

        boolean saved = runForm("TAXTYPE", "REGISTER TAX TYPE", labels, types, hints,
                new int[]{3, 5, 6},
                new String[]{"IDENTIFICATION", "RATE AND FILING", "STATUS AND VALIDATION"});

        if (saved) {
            System.out.println(taxTypes[taxTypeCount - 1].getSummary());
        }
    }


    private static void createObligation() {
        if (obligationCount >= obligations.length) {
            System.out.println("Obligation storage is full (" + obligations.length + ").");
            return;
        }

        String[] labels = {"Obligation ID", "Taxpayer TIN", "Tax type ID",
                "Period start", "Period end", "Due date"};
        String[] types = {"TEXT:2:30", "REF_TAXPAYER", "REF_TAXTYPE",
                "DATEANY", "DATEANY", "DATEANY"};
        String[] hints = {"String, unique, 2-30", "RegisteredTaxpayer, existing ACTIVE TIN",
                "TaxType, existing active ID", "LocalDate, YYYY-MM-DD",
                "LocalDate, YYYY-MM-DD, >= period start", "LocalDate, YYYY-MM-DD, >= period end"};

        boolean saved = runForm("OBLIGATION", "CREATE TAX OBLIGATION", labels, types, hints,
                new int[]{3, 6, 6},
                new String[]{"IDENTIFICATION / PREREQUISITES", "OBLIGATION DETAILS",
                        "VALIDATION / CALCULATIONS"});

        if (saved) {
            System.out.println(obligations[obligationCount - 1].getSummary());
        }
    }

    private static void fileDeclaration() {
        if (declarationCount >= declarations.length) {
            System.out.println("Declaration storage is full (" + declarations.length + ").");
            return;
        }

        String[] labels = {"Declaration ID", "Obligation ID", "Filing date",
                "Taxable amount", "Declared tax", "Status"};
        String[] types = {"TEXT:2:30", "REF_OBLIGATION", "DATE", "MONEY", "MONEY", "AUTO"};
        String[] hints = {"String, unique, 2-30", "TaxObligation, existing active ID",
                "LocalDate, YYYY-MM-DD, not future", "BigDecimal, >= 0, 2 decimals",
                "BigDecimal, = taxable x rate / 100", "String, automatic FILED / LATE"};

        boolean saved = runForm("DECLARATION", "FILE TAX DECLARATION", labels, types, hints,
                new int[]{2, 5, 5},
                new String[]{"IDENTIFICATION / PREREQUISITES", "DECLARATION DETAILS",
                        "VALIDATION / CALCULATIONS"});

        if (saved) {
            System.out.println(declarations[declarationCount - 1].getSummary());
        }
    }

    private static void assessDeclaration() {
        if (assessmentCount >= assessments.length) {
            System.out.println("Assessment storage is full (" + assessments.length + ").");
            return;
        }

        String[] labels = {"Assessment ID", "Declaration ID", "Assessed tax",
                "Adjustment amount", "Assessment date", "Status"};
        String[] types = {"TEXT:2:30", "REF_DECLARATION", "MONEY", "SIGNED", "DATE", "AUTO"};
        String[] hints = {"String, unique, 2-30", "TaxDeclaration, existing, not REJECTED",
                "BigDecimal, >= 0, 2 decimals", "BigDecimal, may be negative, total >= 0",
                "LocalDate, YYYY-MM-DD, not future", "String, automatic DRAFT"};

        boolean saved = runForm("ASSESSMENT", "ASSESS DECLARATION", labels, types, hints,
                new int[]{2, 5, 5},
                new String[]{"IDENTIFICATION / PREREQUISITES", "ASSESSMENT DETAILS",
                        "VALIDATION / CALCULATIONS"});

        if (saved) {
            System.out.println(assessments[assessmentCount - 1].getSummary());
        }
    }


    private static void confirmAssessment() {
        printHeader("CONFIRM ASSESSMENT", "SELECT A DRAFT ASSESSMENT");

        if (assessmentCount == 0) {
            try {
                requirePrerequisite("REF_ASSESSMENT");
            } catch (PrerequisiteMissingException e) {
                if (!offerPrerequisite(e.getMessage(), "REF_ASSESSMENT")) {
                    System.out.println("Cancelled. Nothing was changed.");
                    return;
                }
            }
        }

        int drafts = 0;
        for (int i = 0; i < assessmentCount; i++) {
            if (assessments[i].getStatus().equals("DRAFT")) {
                System.out.println("  " + assessments[i].getSummary());
                drafts++;
            }
        }
        if (drafts == 0) {
            System.out.println("There is no DRAFT assessment waiting for confirmation.");
            return;
        }

        while (true) {
            System.out.print("Assessment ID to confirm (CANCEL to stop) : ");
            String id = readLine().trim();

            if (id.equalsIgnoreCase("CANCEL")) {
                System.out.println("Cancelled. Nothing was changed.");
                return;
            }
            if (id.isEmpty()) {
                System.out.println("   Error: the ID is required.");
                continue;
            }

            TaxAssessment a;
            try {
                a = requireAssessment(id);
            } catch (RecordNotFoundException e) {
                System.out.println("   Error: " + e.getMessage());
                continue;
            }

            a.printDetails();
            System.out.print("[1] CONFIRM   [0] CANCEL : ");
            String choice = readLine().trim();
            if (!choice.equals("1")) {
                System.out.println("Cancelled. Nothing was changed.");
                return;
            }

            try {
                a.validate();
                a.confirm();
                System.out.println("Assessment " + a.getAssessmentId()
                        + " is now CONFIRMED. Payments are allowed.");
            } catch (ValidationException | InvalidStatusTransitionException e) {
                System.out.println("Cannot confirm: " + e.getMessage());
            }
            return;
        }
    }


    private static boolean runForm(String formKey, String title, String[] labels, String[] types,
                                   String[] hints, int[] stepEnd, String[] stepTitles) {
        String[] values = new String[labels.length];
        int dataSteps = stepEnd.length;
        int step = 0;

        while (true) {
            if (step < dataSteps) {
                int start = (step == 0) ? 0 : stepEnd[step - 1];
                int end = stepEnd[step];

                printHeader(title, "STEP " + (step + 1) + " OF " + (dataSteps + 1)
                        + " - " + stepTitles[step]);
                System.out.println("| Type BACK or CANCEL at any field.");

                boolean goBack = false;
                for (int i = start; i < end; i++) {
                    String result = askField(formKey, labels, types, hints, values, i);
                    if (result.equals("CANCEL")) {
                        System.out.println("Cancelled. Nothing was saved.");
                        return false;
                    }
                    if (result.equals("BACK")) {
                        goBack = true;
                        break;
                    }
                }

                if (goBack) {
                    if (step > 0) {
                        step--;
                    } else {
                        System.out.println("This is the first step: you cannot go back.");
                    }
                    continue;
                }


                boolean checksOk = true;
                if (start == end) {
                    String problem = runChecks(formKey, values);
                    if (problem == null) {
                        System.out.println("| All checks passed.");
                    } else {
                        System.out.println("| CHECK FAILED: " + problem);
                        checksOk = false;
                    }
                }


                boolean stay = true;
                while (stay) {
                    System.out.print("NEXT / BACK / CANCEL (or a field number to edit) : ");
                    String nav = readLine().trim().toUpperCase();

                    if (nav.isEmpty() || nav.equals("NEXT")) {
                        if (checksOk) {
                            step++;
                            stay = false;
                        } else {
                            System.out.println("Fix the problem first: type BACK to correct the fields, or CANCEL.");
                        }
                    } else if (nav.equals("BACK")) {
                        if (step > 0) {
                            step--;
                        } else {
                            System.out.println("This is the first step: you cannot go back.");
                        }
                        stay = false;
                    } else if (nav.equals("CANCEL")) {
                        System.out.println("Cancelled. Nothing was saved.");
                        return false;
                    } else {
                        try {
                            int n = Integer.parseInt(nav);
                            if (start == end) {
                                System.out.println("This step has no fields. Type BACK to edit earlier fields.");
                            } else if (n >= start + 1 && n <= end) {
                                String r = askField(formKey, labels, types, hints, values, n - 1);
                                if (r.equals("CANCEL")) {
                                    System.out.println("Cancelled. Nothing was saved.");
                                    return false;
                                }
                            } else {
                                System.out.println("Field number must be between " + (start + 1)
                                        + " and " + end + " for this step.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Type NEXT, BACK, CANCEL or a field number.");
                        }
                    }
                }
            } else {

                printHeader(title, "STEP " + (dataSteps + 1) + " OF " + (dataSteps + 1)
                        + " - REVIEW ALL " + labels.length + " FIELDS");
                for (int i = 0; i < labels.length; i++) {
                    System.out.printf("%02d %-26s [%s] : %s%n", i + 1, labels[i], hints[i], values[i]);
                }
                System.out.println("[1] SAVE   [2] EDIT FIELD   [3] BACK   [0] CANCEL");
                System.out.print("Choice: ");
                String choice = readLine().trim();

                if (choice.equals("1")) {
                    String error = trySave(formKey, values);
                    if (error == null) {
                        System.out.println("Saved successfully.");
                        return true;
                    }
                    System.out.println("Cannot save: " + error);
                    System.out.println("Use [2] EDIT FIELD to correct it.");
                } else if (choice.equals("2")) {
                    System.out.print("Field number to edit (1-" + labels.length + "): ");
                    try {
                        int n = Integer.parseInt(readLine().trim());
                        if (n < 1 || n > labels.length) {
                            System.out.println("Field number must be between 1 and " + labels.length + ".");
                        } else {
                            String r = askField(formKey, labels, types, hints, values, n - 1);
                            if (r.equals("CANCEL")) {
                                System.out.println("Cancelled. Nothing was saved.");
                                return false;
                            }
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Enter a valid field number.");
                    }
                } else if (choice.equals("3")) {
                    step = dataSteps - 1;
                } else if (choice.equals("0")) {
                    System.out.println("Cancelled. Nothing was saved.");
                    return false;
                } else {
                    System.out.println("Unknown option. Choose 1, 2, 3 or 0.");
                }
            }
        }
    }


    private static String askField(String formKey, String[] labels, String[] types,
                                   String[] hints, String[] values, int i) {

        if (types[i].equals("AUTO")) {
            System.out.println(String.format("%02d %s [%s] : %s (computed automatically)",
                    i + 1, labels[i], hints[i], values[i]));
            return "OK";
        }

        if (types[i].startsWith("REF_") && values[i] == null) {
            try {
                requirePrerequisite(types[i]);
            } catch (PrerequisiteMissingException e) {
                if (!offerPrerequisite(e.getMessage(), types[i])) {
                    return "CANCEL";
                }
            }
        }

        while (true) {
            System.out.print(String.format("%02d %s [%s]", i + 1, labels[i], hints[i]));
            if (values[i] != null) {
                System.out.print(" (current: " + values[i] + ", Enter = keep)");
            }
            System.out.print(" : ");

            String input = readLine().trim();

            if (input.equalsIgnoreCase("CANCEL")) {
                return "CANCEL";
            }
            if (input.equalsIgnoreCase("BACK")) {
                return "BACK";
            }
            if (input.isEmpty()) {
                if (values[i] != null) {
                    return "OK";
                }
                System.out.println("   Error: this field is required.");
                continue;
            }

            String value = normalize(types[i], input);
            try {
                checkField(types[i], value);
                uniqueCheck(formKey, i, value);
                if (types[i].startsWith("REF_")) {
                    System.out.println("   Found: "
                            + ((Validatable) findReference(types[i], value)).getSummary());
                }
                values[i] = value;
                return "OK";
            } catch (ValidationException | DuplicateRecordException | RecordNotFoundException e) {
                System.out.println("   Error: " + e.getMessage());
            }
        }
    }

    private static void printHeader(String title, String stepText) {
        System.out.println();
        System.out.println("+------------------ " + title + " ------------------+");
        System.out.println("| " + stepText);
    }


    private static String runChecks(String formKey, String[] v) {
        try {
            switch (formKey) {
                case "OBLIGATION":
                    checkObligation(v);
                    break;
                case "DECLARATION":
                    checkDeclaration(v);
                    break;
                case "ASSESSMENT":
                    checkAssessment(v);
                    break;
                default:
                    break;
            }
            return null;
        } catch (ValidationException | DuplicateRecordException
                 | InvalidTaxPeriodException | InvalidTaxCalculationException e) {
            return e.getMessage();
        }
    }

    private static TaxObligation buildObligation(String[] v) {
        return new TaxObligation(v[0], findTaxpayerByTin(v[1]), findTaxTypeById(v[2]),
                LocalDate.parse(v[3]), LocalDate.parse(v[4]), LocalDate.parse(v[5]));
    }

    private static void checkNoOverlap(TaxObligation o) throws InvalidTaxPeriodException {
        for (int i = 0; i < obligationCount; i++) {
            if (o.overlaps(obligations[i])) {
                throw new InvalidTaxPeriodException("The period overlaps obligation "
                        + obligations[i].getObligationId()
                        + " (same taxpayer and same tax type).");
            }
        }
    }

    private static void checkObligation(String[] v)
            throws ValidationException, InvalidTaxPeriodException {
        TaxObligation o = buildObligation(v);
        o.validate();

        System.out.println("| Taxpayer : " + o.getTaxpayer().getTin() + " - "
                + o.getTaxpayer().getDisplayName());
        System.out.println("| Tax type : " + o.getTaxType().getDisplayName());
        System.out.println("| Period   : " + o.getPeriodStart() + " to " + o.getPeriodEnd()
                + "   Due: " + o.getDueDate());

        o.checkPeriod();
        checkNoOverlap(o);
    }

    private static void checkDeclaration(String[] v)
            throws ValidationException, DuplicateRecordException, InvalidTaxCalculationException {
        TaxObligation ob = findObligationById(v[1]);
        if (ob == null) {
            throw new ValidationException("The obligation no longer exists.");
        }
        if (activeDeclarationFor(ob) != null) {
            throw new DuplicateRecordException("Obligation " + ob.getObligationId()
                    + " already has an active declaration.");
        }

        TaxDeclaration d = new TaxDeclaration(v[0], ob, LocalDate.parse(v[2]),
                new BigDecimal(v[3]), new BigDecimal(v[4]), "FILED");
        d.applyFilingStatus();
        v[5] = d.getStatus();

        System.out.println("| Tax rate      : " + ob.getTaxType().getRate() + " %");
        System.out.println("| Expected tax  : " + d.getExpectedTax() + "  (taxable x rate / 100, HALF_UP)");
        System.out.println("| Declared tax  : " + d.getDeclaredTax());
        System.out.println("| Due date      : " + ob.getDueDate() + "   Filing date: " + d.getFilingDate());
        System.out.println("| Filing status : " + d.getStatus());

        d.checkCalculation();
    }

    private static void checkAssessment(String[] v)
            throws ValidationException, DuplicateRecordException {
        TaxDeclaration d = findDeclarationById(v[1]);
        if (d == null) {
            throw new ValidationException("The declaration no longer exists.");
        }
        if ("REJECTED".equals(d.getStatus())) {
            throw new ValidationException("A rejected declaration cannot be assessed.");
        }
        if (activeAssessmentFor(d) != null) {
            throw new DuplicateRecordException("Declaration " + d.getDeclarationId()
                    + " already has an assessment.");
        }

        BigDecimal assessed = new BigDecimal(v[2]);
        BigDecimal adjustment = new BigDecimal(v[3]);
        BigDecimal total = assessed.add(adjustment);
        LocalDate date = LocalDate.parse(v[4]);

        System.out.println("| Declared tax      : " + d.getDeclaredTax());
        System.out.println("| Assessed tax      : " + assessed);
        System.out.println("| Adjustment amount : " + adjustment);
        System.out.println("| TOTAL             : " + total + "  (assessed tax + adjustment)");

        if (total.signum() < 0) {
            throw new ValidationException("The assessment total cannot be negative.");
        }
        if (date.isBefore(d.getFilingDate())) {
            throw new ValidationException("The assessment date cannot be before the filing date "
                    + d.getFilingDate() + ".");
        }
        v[5] = "DRAFT";
    }

    private static String normalize(String type, String v) {
        if (type.equals("STATUS") || type.equals("YESNO") || type.equals("FREQ")) {
            return v.toUpperCase();
        }
        return v;
    }

    private static void checkField(String type, String v)
            throws ValidationException, RecordNotFoundException {
        String[] p = type.split(":");

        switch (p[0]) {
            case "TEXT": {
                int min = Integer.parseInt(p[1]);
                int max = Integer.parseInt(p[2]);
                if (v.length() < min || v.length() > max) {
                    throw new ValidationException("Must be " + min + "-" + max + " characters.");
                }
                break;
            }
            case "EMAIL":
                if (v.length() > 120
                        || !v.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                    throw new ValidationException("Enter a valid email (max 120 characters).");
                }
                break;
            case "PHONE":
                if (!v.matches("^\\+?[0-9]{10,15}$")) {
                    throw new ValidationException("Phone must be 10-15 digits, optional leading +.");
                }
                break;
            case "DATE": {
                LocalDate d = parseDate(v);
                if (d.isAfter(LocalDate.now())) {
                    throw new ValidationException("The date cannot be in the future.");
                }
                break;
            }
            case "DATEANY":
                parseDate(v);
                break;
            case "TIN":
                if (!v.matches("^[0-9]{9}$")) {
                    throw new ValidationException("TIN must be exactly 9 digits.");
                }
                break;
            case "NATID":
                if (!v.matches("^[0-9]{16}$")) {
                    throw new ValidationException("National ID must be exactly 16 digits.");
                }
                break;
            case "COMPNO":
                if (!v.matches("^[A-Z0-9-]{5,30}$")) {
                    throw new ValidationException(
                            "Use 5-30 uppercase letters, digits or hyphens.");
                }
                break;
            case "STATUS":
                if (!(v.equals("ACTIVE") || v.equals("INACTIVE") || v.equals("SUSPENDED"))) {
                    throw new ValidationException("Status must be ACTIVE, INACTIVE or SUSPENDED.");
                }
                break;
            case "FREQ":
                if (!(v.equals("MONTHLY") || v.equals("QUARTERLY") || v.equals("ANNUAL"))) {
                    throw new ValidationException(
                            "Frequency must be MONTHLY, QUARTERLY or ANNUAL.");
                }
                break;
            case "YESNO":
                if (!(v.equals("YES") || v.equals("NO"))) {
                    throw new ValidationException("Answer YES or NO.");
                }
                break;
            case "MONEY":
                parseMoney(v);
                break;
            case "RATE": {
                BigDecimal r = parseMoney(v);
                if (r.compareTo(new BigDecimal("100")) > 0) {
                    throw new ValidationException("Rate must be between 0 and 100.");
                }
                break;
            }
            case "REF_TAXPAYER":
            case "REF_TAXTYPE":
            case "REF_OBLIGATION":
            case "REF_DECLARATION": {
                Identifiable found = findReference(p[0], v);
                if (found == null) {
                    printAvailable(p[0]);
                    throw new RecordNotFoundException(
                            "No record found for '" + v + "'. Unknown IDs are not accepted.");
                }
                if (!found.isActive()) {
                    throw new ValidationException(inactiveMessage(p[0]));
                }
                break;
            }
            case "SIGNED":
                parseSigned(v);
                break;
            case "AUTO":
                break;
            default:
                break;
        }
    }


    private static LocalDate parseDate(String v) throws ValidationException {
        if (!v.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$")) {
            throw new ValidationException("Use the format YYYY-MM-DD (example 2024-05-31).");
        }
        try {
            return LocalDate.parse(v);
        } catch (DateTimeParseException e) {
            throw new ValidationException("That date does not exist. Use a real date as YYYY-MM-DD.");
        }
    }


    private static BigDecimal parseMoney(String v) throws ValidationException {
        if (v.startsWith("-")) {
            throw new ValidationException("The value cannot be negative.");
        }
        if (!v.matches("^[0-9]+(\\.[0-9]{1,2})?$")) {
            throw new ValidationException("Enter a number with at most 2 decimals (example 1500.50).");
        }
        try {
            BigDecimal m = new BigDecimal(v);
            if (m.compareTo(MAX_MONEY) > 0) {
                throw new ValidationException("The value cannot exceed 999999999999.99.");
            }
            return m;
        } catch (NumberFormatException e) {
            throw new ValidationException("That is not a valid number.");
        }
    }


    private static BigDecimal parseSigned(String v) throws ValidationException {
        if (!v.matches("^-?[0-9]+(\\.[0-9]{1,2})?$")) {
            throw new ValidationException(
                    "Enter a number with at most 2 decimals (example -250.00, or 0 for none).");
        }
        BigDecimal m = new BigDecimal(v);
        if (m.abs().compareTo(MAX_MONEY) > 0) {
            throw new ValidationException("The value cannot exceed 999999999999.99.");
        }
        return m;
    }

    private static void uniqueCheck(String formKey, int idx, String v)
            throws DuplicateRecordException {

        boolean taxpayerForm = formKey.equals("INDIVIDUAL") || formKey.equals("CORPORATE");
        boolean partyForm = taxpayerForm || formKey.equals("OFFICER");

        if (idx == 0 && partyForm && partyIdExists(v)) {
            throw new DuplicateRecordException("Party ID '" + v + "' already exists.");
        }
        if (idx == 6 && taxpayerForm && tinExists(v)) {
            throw new DuplicateRecordException(
                    "TIN " + v + " is already registered (individual or corporate).");
        }
        if (formKey.equals("INDIVIDUAL") && idx == 12 && nationalIdExists(v)) {
            throw new DuplicateRecordException("National ID " + v + " already exists.");
        }
        if (formKey.equals("CORPORATE") && idx == 12 && companyNoExists(v)) {
            throw new DuplicateRecordException("Company registration number " + v + " already exists.");
        }
        if (formKey.equals("OFFICER") && idx == 6 && staffNumberExists(v)) {
            throw new DuplicateRecordException("Staff number " + v + " already exists.");
        }
        if (formKey.equals("TAXTYPE") && idx == 0 && taxTypeIdExists(v)) {
            throw new DuplicateRecordException("Tax type ID " + v + " already exists.");
        }
        if (formKey.equals("TAXTYPE") && idx == 1 && taxCodeExists(v)) {
            throw new DuplicateRecordException("Tax code " + v + " already exists.");
        }


        if (formKey.equals("OBLIGATION") && idx == 0 && obligationIdExists(v)) {
            throw new DuplicateRecordException("Obligation ID " + v + " already exists.");
        }
        if (formKey.equals("DECLARATION") && idx == 0 && declarationIdExists(v)) {
            throw new DuplicateRecordException("Declaration ID " + v + " already exists.");
        }
        if (formKey.equals("DECLARATION") && idx == 1) {
            TaxObligation ob = findObligationById(v);
            if (ob != null && activeDeclarationFor(ob) != null) {
                throw new DuplicateRecordException("Obligation " + ob.getObligationId()
                        + " already has an active declaration ("
                        + activeDeclarationFor(ob).getDeclarationId() + ").");
            }
        }
        if (formKey.equals("ASSESSMENT") && idx == 0 && assessmentIdExists(v)) {
            throw new DuplicateRecordException("Assessment ID " + v + " already exists.");
        }
        if (formKey.equals("ASSESSMENT") && idx == 1) {
            TaxDeclaration d = findDeclarationById(v);
            if (d != null && activeAssessmentFor(d) != null) {
                throw new DuplicateRecordException("Declaration " + d.getDeclarationId()
                        + " already has an assessment (" + activeAssessmentFor(d).getAssessmentId() + ").");
            }
        }
    }

    private static boolean partyIdExists(String id) {
        for (int i = 0; i < individualCount; i++) {
            if (individuals[i].getPartyId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        for (int i = 0; i < corporateCount; i++) {
            if (corporates[i].getPartyId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        for (int i = 0; i < officerCount; i++) {
            if (officers[i].getPartyId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    private static boolean tinExists(String tin) {
        for (int i = 0; i < individualCount; i++) {
            if (individuals[i].getTin().equals(tin)) {
                return true;
            }
        }
        for (int i = 0; i < corporateCount; i++) {
            if (corporates[i].getTin().equals(tin)) {
                return true;
            }
        }
        return false;
    }

    private static boolean nationalIdExists(String nid) {
        for (int i = 0; i < individualCount; i++) {
            if (individuals[i].getNationalId().equals(nid)) {
                return true;
            }
        }
        return false;
    }

    private static boolean companyNoExists(String no) {
        for (int i = 0; i < corporateCount; i++) {
            if (corporates[i].getCompanyRegistrationNo().equals(no)) {
                return true;
            }
        }
        return false;
    }

    private static boolean staffNumberExists(String no) {
        for (int i = 0; i < officerCount; i++) {
            if (officers[i].getStaffNumber().equalsIgnoreCase(no)) {
                return true;
            }
        }
        return false;
    }

    private static boolean taxTypeIdExists(String id) {
        for (int i = 0; i < taxTypeCount; i++) {
            if (taxTypes[i].getTaxTypeId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    private static boolean taxCodeExists(String code) {
        for (int i = 0; i < taxTypeCount; i++) {
            if (taxTypes[i].getTaxCode().equalsIgnoreCase(code)) {
                return true;
            }
        }
        return false;
    }

    private static boolean obligationIdExists(String id) {
        return findObligationById(id) != null;
    }

    private static boolean declarationIdExists(String id) {
        return findDeclarationById(id) != null;
    }

    private static boolean assessmentIdExists(String id) {
        return findAssessmentById(id) != null;
    }


    private static String trySave(String formKey, String[] v) {
        try {
            switch (formKey) {
                case "INDIVIDUAL":
                    saveIndividual(v);
                    break;
                case "CORPORATE":
                    saveCorporate(v);
                    break;
                case "OFFICER":
                    saveOfficer(v);
                    break;
                case "TAXTYPE":
                    saveTaxType(v);
                    break;
                case "OBLIGATION":
                    saveObligation(v);
                    break;
                case "DECLARATION":
                    saveDeclaration(v);
                    break;
                case "ASSESSMENT":
                    saveAssessment(v);
                    break;

                default:
                    return "Unknown form.";
            }
            return null;
        } catch (ValidationException | DuplicateRecordException
                 | InvalidTaxPeriodException | InvalidTaxCalculationException e) {
            return e.getMessage();
        }
    }

    private static void saveIndividual(String[] v)
            throws ValidationException, DuplicateRecordException {
        if (individualCount >= individuals.length) {
            throw new ValidationException("Storage is full. Cannot save.");
        }
        if (partyIdExists(v[0])) {
            throw new DuplicateRecordException("Party ID '" + v[0] + "' already exists.");
        }
        if (tinExists(v[6])) {
            throw new DuplicateRecordException("TIN " + v[6] + " is already registered.");
        }
        if (nationalIdExists(v[12])) {
            throw new DuplicateRecordException("National ID " + v[12] + " already exists.");
        }

        IndividualTaxpayer t = new IndividualTaxpayer(
                v[0], v[1], v[2], v[3], v[4], LocalDate.parse(v[5]),
                v[6], LocalDate.parse(v[7]), v[8], v[9], v[10], v[11],
                v[12], v[13], v[14], LocalDate.parse(v[15]), new BigDecimal(v[16]),
                v[17].equals("YES"));

        t.validate();

        individuals[individualCount] = t;
        individualCount++;
    }

    private static void saveCorporate(String[] v)
            throws ValidationException, DuplicateRecordException {
        if (corporateCount >= corporates.length) {
            throw new ValidationException("Storage is full. Cannot save.");
        }
        if (partyIdExists(v[0])) {
            throw new DuplicateRecordException("Party ID '" + v[0] + "' already exists.");
        }
        if (tinExists(v[6])) {
            throw new DuplicateRecordException("TIN " + v[6] + " is already registered.");
        }
        if (companyNoExists(v[12])) {
            throw new DuplicateRecordException("Company registration number " + v[12] + " already exists.");
        }

        CorporateTaxpayer c = new CorporateTaxpayer(
                v[0], v[1], v[2], v[3], v[4], LocalDate.parse(v[5]),
                v[6], LocalDate.parse(v[7]), v[8], v[9], v[10], v[11],
                v[12], v[13], LocalDate.parse(v[14]), v[15], v[16], new BigDecimal(v[17]));

        c.validate();

        corporates[corporateCount] = c;
        corporateCount++;
    }

    private static void saveOfficer(String[] v)
            throws ValidationException, DuplicateRecordException {
        if (officerCount >= officers.length) {
            throw new ValidationException("Storage is full. Cannot save.");
        }
        if (partyIdExists(v[0])) {
            throw new DuplicateRecordException("Party ID '" + v[0] + "' already exists.");
        }
        if (staffNumberExists(v[6])) {
            throw new DuplicateRecordException("Staff number " + v[6] + " already exists.");
        }

        RevenueOfficer o = new RevenueOfficer(
                v[0], v[1], v[2], v[3], v[4], LocalDate.parse(v[5]),
                v[6], v[7], v[8], LocalDate.parse(v[9]), new BigDecimal(v[10]),
                v[11].equals("YES"));

        o.validate();

        officers[officerCount] = o;
        officerCount++;
    }

    private static void saveTaxType(String[] v)
            throws ValidationException, DuplicateRecordException {
        if (taxTypeCount >= taxTypes.length) {
            throw new ValidationException("Storage is full. Cannot save.");
        }
        if (taxTypeIdExists(v[0])) {
            throw new DuplicateRecordException("Tax type ID " + v[0] + " already exists.");
        }
        if (taxCodeExists(v[1])) {
            throw new DuplicateRecordException("Tax code " + v[1] + " already exists.");
        }

        TaxType t = new TaxType(v[0], v[1], v[2], new BigDecimal(v[3]), v[4], v[5].equals("YES"));

        t.validate();

        taxTypes[taxTypeCount] = t;
        taxTypeCount++;
    }

    private static void saveObligation(String[] v)
            throws ValidationException, DuplicateRecordException, InvalidTaxPeriodException {
        if (obligationCount >= obligations.length) {
            throw new ValidationException("Storage is full. Cannot save.");
        }
        if (obligationIdExists(v[0])) {
            throw new DuplicateRecordException("Obligation ID " + v[0] + " already exists.");
        }

        TaxObligation o = buildObligation(v);
        o.validate();
        o.checkPeriod();
        checkNoOverlap(o);

        obligations[obligationCount] = o;
        obligationCount++;
    }

    private static void saveDeclaration(String[] v)
            throws ValidationException, DuplicateRecordException, InvalidTaxCalculationException {
        if (declarationCount >= declarations.length) {
            throw new ValidationException("Storage is full. Cannot save.");
        }
        if (declarationIdExists(v[0])) {
            throw new DuplicateRecordException("Declaration ID " + v[0] + " already exists.");
        }

        TaxObligation ob = findObligationById(v[1]);
        if (ob == null) {
            throw new ValidationException("The obligation no longer exists.");
        }
        if (!ob.isActive()) {
            throw new ValidationException("The obligation is not active.");
        }
        if (activeDeclarationFor(ob) != null) {
            throw new DuplicateRecordException("Obligation " + ob.getObligationId()
                    + " already has an active declaration.");
        }

        TaxDeclaration d = new TaxDeclaration(v[0], ob, LocalDate.parse(v[2]),
                new BigDecimal(v[3]), new BigDecimal(v[4]), "FILED");
        d.applyFilingStatus(); // LATE when filed after the due date; the filing date is never changed
        d.validate();
        d.checkCalculation();

        declarations[declarationCount] = d;
        declarationCount++;
    }

    private static void saveAssessment(String[] v)
            throws ValidationException, DuplicateRecordException {
        if (assessmentCount >= assessments.length) {
            throw new ValidationException("Storage is full. Cannot save.");
        }
        if (assessmentIdExists(v[0])) {
            throw new DuplicateRecordException("Assessment ID " + v[0] + " already exists.");
        }

        TaxDeclaration d = findDeclarationById(v[1]);
        if (d == null) {
            throw new ValidationException("The declaration no longer exists.");
        }
        if (activeAssessmentFor(d) != null) {
            throw new DuplicateRecordException("Declaration " + d.getDeclarationId()
                    + " already has an assessment.");
        }

        TaxAssessment a = new TaxAssessment(v[0], d, new BigDecimal(v[2]),
                new BigDecimal(v[3]), LocalDate.parse(v[4]), "DRAFT");
        a.validate();

        assessments[assessmentCount] = a;
        assessmentCount++;
        d.setStatus("ASSESSED"); // changed only AFTER the assessment is safely stored
    }


    private static void requirePrerequisite(String kind) throws PrerequisiteMissingException {
        if (prerequisiteCount(kind) == 0) {
            switch (kind) {
                case "REF_TAXPAYER":
                    throw new PrerequisiteMissingException("No taxpayer is registered yet.");
                case "REF_TAXTYPE":
                    throw new PrerequisiteMissingException("No tax type is registered yet.");
                case "REF_OBLIGATION":
                    throw new PrerequisiteMissingException("No tax obligation exists yet.");
                case "REF_DECLARATION":
                    throw new PrerequisiteMissingException("No tax declaration exists yet.");
                case "REF_ASSESSMENT":
                    throw new PrerequisiteMissingException("No tax assessment exists yet.");
                default:
                    break;
            }
        }
    }

    private static int prerequisiteCount(String kind) {
        switch (kind) {
            case "REF_TAXPAYER":
                return individualCount + corporateCount;
            case "REF_TAXTYPE":
                return taxTypeCount;
            case "REF_OBLIGATION":
                return obligationCount;
            case "REF_DECLARATION":
                return declarationCount;
            case "REF_ASSESSMENT":
                return assessmentCount;
            default:
                return 1;
        }
    }


    private static boolean offerPrerequisite(String problem, String kind) {
        System.out.println("   " + problem);
        while (true) {
            System.out.println("   [1] Register prerequisite now   [0] Cancel");
            System.out.print("   Choice: ");
            String c = readLine().trim();

            if (c.equals("0")) {
                return false;
            }
            if (c.equals("1")) {
                int before = prerequisiteCount(kind);
                registerPrerequisite(kind);
                if (prerequisiteCount(kind) > before) {
                    System.out.println("   Prerequisite saved. Resuming your form (entered values are kept).");
                    return true;
                }
                System.out.println("   The prerequisite was not saved.");
            } else {
                System.out.println("   Unknown choice. Enter 1 or 0.");
            }
        }
    }

    private static void registerPrerequisite(String kind) {
        switch (kind) {
            case "REF_TAXPAYER": {
                System.out.println("   [1] Individual taxpayer   [2] Corporate taxpayer");
                System.out.print("   Choice: ");
                String c = readLine().trim();
                if (c.equals("1")) {
                    registerIndividual();
                } else if (c.equals("2")) {
                    registerCorporate();
                } else {
                    System.out.println("   Unknown choice.");
                }
                break;
            }
            case "REF_TAXTYPE":
                registerTaxType();
                break;
            case "REF_OBLIGATION":
                createObligation();
                break;
            case "REF_DECLARATION":
                fileDeclaration();
                break;
            case "REF_ASSESSMENT":
                assessDeclaration();
                break;
            default:
                break;
        }
    }


    private static Identifiable findReference(String type, String v) {
        switch (type) {
            case "REF_TAXPAYER":
                return findTaxpayerByTin(v);
            case "REF_TAXTYPE":
                return findTaxTypeById(v);
            case "REF_OBLIGATION":
                return findObligationById(v);
            case "REF_DECLARATION":
                return findDeclarationById(v);
            default:
                return null;
        }
    }

    private static String inactiveMessage(String type) {
        switch (type) {
            case "REF_TAXPAYER":
                return "The taxpayer must be ACTIVE.";
            case "REF_TAXTYPE":
                return "The tax type must be active.";
            case "REF_OBLIGATION":
                return "The obligation is not active (its taxpayer and tax type must be active).";
            case "REF_DECLARATION":
                return "A rejected declaration cannot be assessed.";
            default:
                return "The record is not active.";
        }
    }

    private static void printAvailable(String type) {
        System.out.println("   Available records:");
        switch (type) {
            case "REF_TAXPAYER":
                for (int i = 0; i < individualCount; i++) {
                    System.out.println("   - " + individuals[i].getTin() + " | "
                            + individuals[i].getDisplayName() + " | " + individuals[i].getTaxpayerStatus());
                }
                for (int i = 0; i < corporateCount; i++) {
                    System.out.println("   - " + corporates[i].getTin() + " | "
                            + corporates[i].getDisplayName() + " | " + corporates[i].getTaxpayerStatus());
                }
                break;
            case "REF_TAXTYPE":
                for (int i = 0; i < taxTypeCount; i++) {
                    System.out.println("   - " + taxTypes[i].getTaxTypeId() + " | "
                            + taxTypes[i].getDisplayName() + " | "
                            + (taxTypes[i].isActive() ? "ACTIVE" : "INACTIVE"));
                }
                break;
            case "REF_OBLIGATION":
                for (int i = 0; i < obligationCount; i++) {
                    System.out.println("   - " + obligations[i].getDisplayName());
                }
                break;
            case "REF_DECLARATION":
                for (int i = 0; i < declarationCount; i++) {
                    System.out.println("   - " + declarations[i].getDeclarationId() + " | "
                            + declarations[i].getStatus());
                }
                break;
            default:
                break;
        }
    }

    private static RegisteredTaxpayer findTaxpayerByTin(String tin) {
        for (int i = 0; i < individualCount; i++) {
            if (individuals[i].getTin().equals(tin)) {
                return individuals[i];
            }
        }
        for (int i = 0; i < corporateCount; i++) {
            if (corporates[i].getTin().equals(tin)) {
                return corporates[i];
            }
        }
        return null;
    }

    private static TaxType findTaxTypeById(String id) {
        for (int i = 0; i < taxTypeCount; i++) {
            if (taxTypes[i].getTaxTypeId().equalsIgnoreCase(id)) {
                return taxTypes[i];
            }
        }
        return null;
    }

    private static TaxObligation findObligationById(String id) {
        for (int i = 0; i < obligationCount; i++) {
            if (obligations[i].getObligationId().equalsIgnoreCase(id)) {
                return obligations[i];
            }
        }
        return null;
    }

    private static TaxDeclaration findDeclarationById(String id) {
        for (int i = 0; i < declarationCount; i++) {
            if (declarations[i].getDeclarationId().equalsIgnoreCase(id)) {
                return declarations[i];
            }
        }
        return null;
    }

    private static TaxAssessment findAssessmentById(String id) {
        for (int i = 0; i < assessmentCount; i++) {
            if (assessments[i].getAssessmentId().equalsIgnoreCase(id)) {
                return assessments[i];
            }
        }
        return null;
    }

    private static TaxAssessment requireAssessment(String id) throws RecordNotFoundException {
        TaxAssessment a = findAssessmentById(id);
        if (a == null) {
            throw new RecordNotFoundException("Assessment '" + id + "' does not exist.");
        }
        return a;
    }


    private static TaxDeclaration activeDeclarationFor(TaxObligation ob) {
        for (int i = 0; i < declarationCount; i++) {
            if (declarations[i].getObligation() == ob && declarations[i].isActive()) {
                return declarations[i];
            }
        }
        return null;
    }


    private static TaxAssessment activeAssessmentFor(TaxDeclaration d) {
        for (int i = 0; i < assessmentCount; i++) {
            if (assessments[i].getDeclaration() == d && assessments[i].isActive()) {
                return assessments[i];
            }
        }
        return null;
    }

    private static String readLine() {
        if (!sc.hasNextLine()) {
            System.out.println();
            System.out.println("Input closed. Exiting.");
            System.exit(0);
        }
        return sc.nextLine();
    }

    private static String[] concat(String[] a, String[] b) {
        String[] result = new String[a.length + b.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i];
        }
        for (int i = 0; i < b.length; i++) {
            result[a.length + i] = b[i];
        }
        return result;
    }
}