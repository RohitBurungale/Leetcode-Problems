// //Given an integer array nums sorted in non-decreasing order,
//  remove the duplicates in-place such that each unique element appears only once.
//  The relative order of the elements should be kept the same.

// Consider the number of unique elements in nums to be k​​​​​​​​​​​​​​. After removing duplicates,
//  return the number of unique elements k.

// The first k elements of nums should contain the unique numbers in sorted order.
//  The remaining elements beyond index k - 1 can be ignored.





public class Solution {
    public int removeDuplicates(int[] nums) {
        if (nums == null || nums.length == 0) return 0;
        int i = 1;
        for (int j = 1; j < nums.length; j++) {
            if (nums[j] != nums[j - 1]) {
                nums[i] = nums[j];
                i++;
            }
        }
        return i;
    }
}
