// LeetCode Problem 283: Move Zeroes
// Given an array nums, write a function to move all 0's to the end of
// it while maintaining the relative order of the non-zero elements.
// Note that you must do this in-place without making a copy of the array.

public class Solution8{
    public void moveZeroes(int[] nums) {
        int index = 0;

        // Move all non-zero values to the front
        for (int num : nums) {
            if (num != 0) {
                nums[index] = num;
                index++;
            }
        }

        // Fill the remaining positions with 0
        while (index < nums.length) {
            nums[index] = 0;
            index++;
        }
    }
    public static void main(String[] args) {
        Solution8 sol = new Solution8();
        int[] nums = {0, 1, 0, 3, 12};
        sol.moveZeroes(nums);
        // Output the modified array
        for (int num : nums) {
            System.out.print(num + " ");
        }
        // Expected Output: 1 3 12 0 0
    }
}
 

