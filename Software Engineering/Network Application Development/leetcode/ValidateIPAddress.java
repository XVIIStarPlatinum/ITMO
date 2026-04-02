// LeetCode #468

class ValidateIPAddress {
    public String validIPAddress(String queryIP) {
        if (isIPv4(queryIP)) {
            return "IPv4";
        } else if (isIPv6(queryIP)) {
            return "IPv6";
        }
        return "Neither";
    }

    private boolean isIPv4(String queryIP) {
        if (queryIP.endsWith(".")) {
            return false;
        }
        String[] ipAddressParts = queryIP.split("\\.");
        if (ipAddressParts.length != 4) {
            return false;
        }

        for (String n : ipAddressParts) {
            if (n.length() == 0 || (n.length() > 1 && n.charAt(0) == '0')){
                return false;
            }
            int val = convertToInteger(n);
            if (val < 0 || val > 255) {
                return false;
            }
        }
        return true;
    }

    private boolean isIPv6(String queryIP) {
        if (queryIP.endsWith(":")) {
            return false;
        }
        String[] ipAddressParts = queryIP.split(":");
        if (ipAddressParts.length != 8) {
            return false;
        }
        for (String h : ipAddressParts) {
            if (h.length() < 1 || h.length() > 4) {
                return false;
            }
            for (char c : h.toCharArray()) {
                if (!Character.isDigit(c) && !"0123456789abcdefABCDEF".contains(String.valueOf(c))) {
                    return false;
                }
            }
        }
        return true;
    }

    private int convertToInteger(String n) {
        int result = 0;
        for (char c : n.toCharArray()) {
            if (!Character.isDigit(c)) {
                return -1;
            }
            result = result * 10 + (c - '0');
            if (result > 255) {
                return result;
            }
        }
        return result;
    }
}
