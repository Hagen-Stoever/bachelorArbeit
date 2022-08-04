package com.commerzbank.heartbeatapiprovider.utils;

public class KeyStoreUtils {

    public static String storeTypeByExtension(String filename) {
        return storeTypeByExtension(filename, "PKCS12");
    }

    // An alternative approach would be to try to load the file with each store type.
    // That would exclude false positives, but also prevent detection if the password is wrong.
    public static String storeTypeByExtension(String filename, String defaultType) {
        if (filename != null && filename.contains(".")) {
            String fileExtension = filename.substring(filename.lastIndexOf(".") + 1);
            switch (fileExtension.toLowerCase()) {
                case "jks":
                    return "JKS";
                case "pfx":
                case "p12":
                    return "PKCS12";
                default:
                    return defaultType;
            }
        }
        return defaultType;
    }
}
