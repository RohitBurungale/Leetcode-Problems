// LeetCode Problem 36: Valid Sudoku
// Determine if a 9x9 Sudoku board is valid.
// Only the filled cells need to be validated according to the rules:
// Each row must contain the digits 1-9 without repetition.
// Each column must contain the digits 1-9 without repetition.
// Each of the nine 3x3 sub-boxes of the grid must contain the digits 1-9 without repetition.
import java.util.HashSet;

class Solution10 {
    public boolean isValidSudoku(char[][] board) {
        // Sets for tracking seen numbers
        HashSet<String> seen = new HashSet<>();

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                char value = board[r][c];

                if (value == '.') continue; // skip empty cells

                // Build keys
                String rowKey = "row" + r + value;
                String colKey = "col" + c + value;
                String boxKey = "box" + (r / 3) + (c / 3) + value;

                // Check duplicates
                if (seen.contains(rowKey) || seen.contains(colKey) || seen.contains(boxKey)) {
                    return false;
                }

                // Mark as seen
                seen.add(rowKey);
                seen.add(colKey);
                seen.add(boxKey);
            }
        }

        return true;
    }
    public static void main(String[] args) {
        Solution10 sol = new Solution10();
        char[][] board = {
            {'5','3','.','.','7','.','.','.','.'},
            {'6','.','.','1','9','5','.','.','.'},
            {'.','9','8','.','.','.','.','6','.'},
            {'8','.','.','.','6','.','.','.','3'},
            {'4','.','.','8','.','3','.','.','1'},
            {'7','.','.','.','2','.','.','.','6'},
            {'.','6','.','.','.','.','2','8','.'},
            {'.','.','.','4','1','9','.','.','5'},
            {'.','.','.','.','8','.','.','7','9'}
        };
        boolean result = sol.isValidSudoku(board);
        System.out.println("Is the Sudoku board valid? " + result);
    
    }
}
