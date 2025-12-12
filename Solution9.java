// Solution9.java
// LeetCode Problem 1: Two Sum
// Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.
// You may assume that each input would have exactly one solution, and you may not use the same element twice.
import java.util.HashMap;

class Solution9{
    public int[] twoSum(int[] nums, int target) {
        HashMap<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];

            if (map.containsKey(complement)) {
                return new int[] { map.get(complement), i };
            }

            map.put(nums[i], i);
        }

        return new int[0]; 
    }
    public static void main(String[] args) {
        Solution9 sol = new Solution9();
        int[] nums = {2, 7, 11, 15};
        int target = 9;
        int[] result = sol.twoSum(nums, target);
        
        System.out.println("Indices: [" + result[0] + ", " + result[1] + "]");
        
    }
}
