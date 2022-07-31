package com.linxb.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Test {
    public static List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> res=new ArrayList<Integer>();
        int[][] dp = new int[matrix.length][matrix[0].length];


        for(int x = 0;x<matrix.length;x++){
            for(int y = 0; y<matrix[0].length;y++){
                dp[x][y] = 0;
            }
        }

        int i = 0, j = 0;
        while(true){
            // 退出条件
            if((j==matrix[0].length-1 || dp[i][j+1]==1)&&(i==matrix.length-1||dp[i+1][j]==1)&&(j==0||dp[i][j-1]==1)&&(i==0||dp[i-1][j]==1)){
                // 遍历完成
                res.add(matrix[i][j]);
                return res;
            }
            // 向右
            while(j<matrix[0].length){
                if(j!=matrix[0].length-1){
                    if(dp[i][j+1]!=1){
                        dp[i][j]=1;
                        res.add(matrix[i][j]);
                        j++;
                    }
                    else{
                        dp[i][j]=1;
                        res.add(matrix[i][j]);
                        break;
                    }
                }else{
                    dp[i][j]=1;
                    res.add(matrix[i][j]);

                    break;
                }

            }
            if(i<matrix.length){
                i++;
            }else{
                return res;
            }
            // 向下
            while(i<matrix.length){
                if(i!=matrix.length-1){
                    if(dp[i+1][j]!=1){
                        dp[i][j]=1;
                        res.add(matrix[i][j]);
                        i++;
                    }else{
                        dp[i][j]=1;
                        res.add(matrix[i][j]);
                        break;
                    }
                }else{
                    dp[i][j]=1;
                    res.add(matrix[i][j]);
                    break;

                }

            }
            if(j>-1){
                j--;
            }else{
                return res;
            }
            // 向左
            while(j>-1){
                if(j!=0){
                    if(dp[i][j-1]!=1){
                        dp[i][j]=1;
                        res.add(matrix[i][j]);
                        j--;
                    }
                    else{
                        dp[i][j]=1;
                        res.add(matrix[i][j]);
                        break;
                    }
                }else{
                    dp[i][j]=1;
                    res.add(matrix[i][j]);
                    break;

                }

            }
            if(i>-1){
                i--;
            }else{
                return res;
            }
            // 向上
            while(i>-1){
                if(i!=0){
                    if(dp[i-1][j]!=1){
                        dp[i][j]=1;
                        res.add(matrix[i][j]);
                        i--;
                    }else{
                        dp[i][j]=1;
                        res.add(matrix[i][j]);
                        break;
                    }
                }else{
                    dp[i][j]=1;
                    res.add(matrix[i][j]);
                    break;

                }

            }
            if(j<matrix[0].length){
                j++;
            }else{
                return res;
            }

        }

    }

    public static void main(String[] args) {
        String s = "dasgasgasdgasgawergaw";
        String t = "daw";
// 滑动窗口
        int left=0,right=0,court = 0;
        HashMap temp = new HashMap();
        for(int i = 0;i < t.length();i++){
            temp.put(t.charAt(i),0);
        }
        // 扩大窗口
        while(right < s.length()){
            char char_right = s.charAt(right);
//            if(court == t.length()){
//                break;
//            }
            if(temp.containsKey(char_right)){
                temp.put(char_right,(int) temp.get(char_right)+1);
                int end = right;
                court++;
            }
            right++;
        }

        System.out.println(s.substring(left,right));
        // 缩小窗口
        while(left < right){
            char char_left = s.charAt(left);
            if(s.substring(left+1,right).contains(""+char_left)==false && temp.containsKey(char_left)){
                temp.put(char_left,0);
                System.out.println(s.substring(left,right));
                break;
            }else{
                left++;
            }
        }
        System.out.println("fasdgasg");
    }

//        int[][] matrix = {{1,2,3,4},{5,6,7,8},{9,10,11,12}};
//        System.out.println(Test.spiralOrder(matrix));
    }