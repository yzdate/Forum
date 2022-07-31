package com.linxb.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Test {
    public static List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> res = new ArrayList<Integer>();
        int[][] dp = new int[matrix.length][matrix[0].length];


        for (int x = 0; x < matrix.length; x++) {
            for (int y = 0; y < matrix[0].length; y++) {
                dp[x][y] = 0;
            }
        }

        int i = 0, j = 0;
        while (true) {
            // 退出条件
            if ((j == matrix[0].length - 1 || dp[i][j + 1] == 1) && (i == matrix.length - 1 || dp[i + 1][j] == 1) && (j == 0 || dp[i][j - 1] == 1) && (i == 0 || dp[i - 1][j] == 1)) {
                // 遍历完成
                res.add(matrix[i][j]);
                return res;
            }
            // 向右
            while (j < matrix[0].length) {
                if (j != matrix[0].length - 1) {
                    if (dp[i][j + 1] != 1) {
                        dp[i][j] = 1;
                        res.add(matrix[i][j]);
                        j++;
                    } else {
                        dp[i][j] = 1;
                        res.add(matrix[i][j]);
                        break;
                    }
                } else {
                    dp[i][j] = 1;
                    res.add(matrix[i][j]);

                    break;
                }

            }
            if (i < matrix.length) {
                i++;
            } else {
                return res;
            }
            // 向下
            while (i < matrix.length) {
                if (i != matrix.length - 1) {
                    if (dp[i + 1][j] != 1) {
                        dp[i][j] = 1;
                        res.add(matrix[i][j]);
                        i++;
                    } else {
                        dp[i][j] = 1;
                        res.add(matrix[i][j]);
                        break;
                    }
                } else {
                    dp[i][j] = 1;
                    res.add(matrix[i][j]);
                    break;

                }

            }
            if (j > -1) {
                j--;
            } else {
                return res;
            }
            // 向左
            while (j > -1) {
                if (j != 0) {
                    if (dp[i][j - 1] != 1) {
                        dp[i][j] = 1;
                        res.add(matrix[i][j]);
                        j--;
                    } else {
                        dp[i][j] = 1;
                        res.add(matrix[i][j]);
                        break;
                    }
                } else {
                    dp[i][j] = 1;
                    res.add(matrix[i][j]);
                    break;

                }

            }
            if (i > -1) {
                i--;
            } else {
                return res;
            }
            // 向上
            while (i > -1) {
                if (i != 0) {
                    if (dp[i - 1][j] != 1) {
                        dp[i][j] = 1;
                        res.add(matrix[i][j]);
                        i--;
                    } else {
                        dp[i][j] = 1;
                        res.add(matrix[i][j]);
                        break;
                    }
                } else {
                    dp[i][j] = 1;
                    res.add(matrix[i][j]);
                    break;

                }

            }
            if (j < matrix[0].length) {
                j++;
            } else {
                return res;
            }

        }

    }

    public static String minWindow(String s, String t) {
        if(s.length() < t.length()){
            return new String();
        }
        if(s.equals(t)){
            return s;
        }
        int[] window = new int[128];
        int[] need = new int[128];
        for(int i = 0; i< t.length();i++){
            char e = t.charAt(i);
            need[e]++;
        }


        int left = 0;
        int right = 0;
        int vaild = 0;
        int[] res = new int[3];
        res[0] = s.length();
        while(right < s.length()){
            char c = (char) s.charAt(right);
            if(t.contains(""+c)){
                if(window[c]<need[c]){
                    vaild++;
                }
                window[c]++;

            }
            right++;
            if(vaild== t.length() && t.contains(""+c)){
                while(left<right){
                    char d = (char) s.charAt(left);
                    if(t.contains(""+d)){
                        if(window[d]==need[d]){
                            break;
                        }else{
                            window[d]--;

                        }
                    }
                    left++;

                }
                if(right-left<=res[0]){
                    res[0] = right-left;
                    res[1] = left;
                    res[2] = right;
                }
            }
        }
        return s.substring(res[1],res[2]);
    }

    public static boolean checkInclusion(String s1, String s2) {
        int[] need = new int[128];
        int[] window = new int[128];
        for(int i = 0;i<s2.length();i++){
            need[s2.charAt(i)]++;
        }
        int right = 0;
        boolean find = false;
        while(right < s1.length()){
            char c = s1.charAt(right);
            if(s2.contains(c+"")){
                find=true;
                for(int i = 0;i<s2.length();i++){
                    if(right+i>=s1.length()){
                        find=false;
                        break;
                    }
                    char e = s1.charAt(right+i);
                    window[e]++;
                }
                for(int i = 0;i<s2.length();i++){
                    if(need[s2.charAt(i)]!=window[s2.charAt(i)]){
                        find=false;
                        break;
                    }
                }
                if(find == true){
                    return true;
                }
            }
            right++;
            for(int i = 0;i<s2.length();i++){
                window[s2.charAt(i)]=0;
            }

        }
        return false;
    }

    public static void main(String[] args) {
        String s = "bbaa";
        String t = "aba";
        System.out.println(Test.checkInclusion(s,t));

//        int[][] matrix = {{1,2,3,4},{5,6,7,8},{9,10,11,12}};
//        System.out.println(Test.spiralOrder(matrix));
    }
}