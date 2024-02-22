package com.way.threes_company_backend.utils;

import java.util.List;

public class AlgoUtils {
    public static int minDistance(List<String> listTag1, List<String> listTag2) {
        int n = listTag1.size();
        int m = listTag2.size();

        if (n * m == 0)
            return n + m;

        int[][] d = new int[n + 1][m + 1];
        for (int i = 0; i < n + 1; i++) {
            d[i][0] = i;
        }

        for (int j = 0; j < m + 1; j++) {
            d[0][j] = j;
        }

        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < m + 1; j++) {
                int left = d[i - 1][j] + 1;
                int down = d[i][j - 1] + 1;
                int left_down = d[i - 1][j - 1];
                if (!listTag1.get(i - 1).equals(listTag2.get(j - 1)))   // 比较是否元素相等  集合里面的元素  使用equals进行比较
                    left_down += 1;
                d[i][j] = Math.min(left, Math.min(down, left_down));
            }
        }
        return d[n][m];
    }
}
