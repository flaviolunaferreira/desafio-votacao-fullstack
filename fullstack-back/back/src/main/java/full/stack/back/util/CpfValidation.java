package full.stack.back.util;

public class CpfValidation {
    public static boolean isValid(String cpf) {
        cpf = cpf.replaceAll("[^0-9]", "");
        if (cpf.length() != 11) return false;
        if (cpf.matches("(\\d)\\1{10}")) return false;
        int[] digits = cpf.chars().map(c -> c - '0').toArray();
        return validateDigit(digits, 9) && validateDigit(digits, 10);
    }

    private static boolean validateDigit(int[] digits, int pos) {
        int sum = 0;
        for (int i = 0; i < pos; i++) {
            sum += digits[i] * (pos + 1 - i);
        }
        int mod = sum % 11;
        int digit = mod < 2 ? 0 : 11 - mod;
        return digit == digits[pos];
    }
}
