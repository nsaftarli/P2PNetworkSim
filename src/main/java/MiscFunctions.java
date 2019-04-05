public class MiscFunctions {
    public MiscFunctions(){}

    private static final int NSERVERS = 4;

    /**
     * Takes a key and hashes it.
     * @param key The key to hash
     * @return The final hashed value in range [0,2^4 - 1]
     */
    public static int hashFunction(String key) {
        char[] keyChars = key.toCharArray();
        int acc = 0;
        for(char c: keyChars) {
            acc += (int) c;
        }

        int hashVal = acc % NSERVERS;
        return hashVal + 1;

    }
}
