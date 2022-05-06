public class Solution { 
    public String reverseStr(String str) {
        StringBuilder sb = new StringBuilder(str);
        return sb.reverse().toString();
    }
public static void main(String[] args) {  
    Solution s = new Solution(); 
    String str = "abcdefghaaa"; 
    String rstr = "aaahgfedcba"; 
    if (s.reverseStr(str).equals(rstr)) { 
        System.out.println("Test OK");
    } else {
        System.out.println("Test Failed");
    } 
}

}