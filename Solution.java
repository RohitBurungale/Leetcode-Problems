import java.util.*;

public class Solution{

    public static int[] twoSum(int[] nums, int target)
     {
        HashMap<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];

            if (map.containsKey(complement)) {
                return new int[] { map.get(complement), i };
            }

            map.put(nums[i], i);
        }

        return new int[] {}; 
    }
        
        public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Enter the array size:");
        int size = sc.nextInt();
        int nums[] = new int[size];

        System.out.println("Enter array elements:");
        for (int i = 0; i < size; i++) {
            nums[i] = sc.nextInt();
        }

        System.out.println("Enter target:");
        int target = sc.nextInt();

        int[] result = twoSum(nums, target);

        System.out.println("Indices of the two numbers are: " + result[0] + " and " + result[1]);
    }

}
