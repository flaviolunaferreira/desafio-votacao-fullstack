package full.stack.back.util;

public class CpfValidation {

    public static boolean isValid(String cpf) {
        // Remove caracteres não numéricos
        cpf = cpf.replaceAll("[^0-9]", "");

        // Verifica tamanho
        if (cpf.length() != 11) {
            return false;
        }

        // Verifica se todos os dígitos são iguais
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        // Validação dos dígitos verificadores
        try {
            int[] digits = cpf.chars().map(c -> c - '0').toArray();
            return isValidDigit(digits, 9) && isValidDigit(digits, 10);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isValidDigit(int[] digits, int position) {
        int sum = 0;
        int weight = position + 1;
        for (int i = 0; i < position; i++) {
            sum += digits[i] * weight--;
        }
        int remainder = sum % 11;
        int expectedDigit = remainder < 2 ? 0 : 11 - remainder;
        return digits[position] == expectedDigit;
    }

}
