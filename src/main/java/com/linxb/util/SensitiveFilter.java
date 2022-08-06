package com.linxb.util;

import org.apache.commons.lang3.CharUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 敏感词过滤工具
 */

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    // 替换符
    private static final String REPLACEMENT = "***";

    // 根节点
    private TrieNode rootNode = new TrieNode();

    // 根据敏感词，初始化前缀树
    @PostConstruct
    public void init(){
        // 获取任意类的类加载器
        try{
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
            String keyword;
            while((keyword = reader.readLine())!=null){
                // 添加到前缀树
                this.addKeyword(keyword);
            }

        } catch (IOException e) {
            logger.error("加载敏感词文件失败"+ e.getMessage());
        }

    }

    // 定义前缀树
    private class TrieNode{

        // 关键词结束表示
        private boolean isKeywordEnd=false;

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        // 子节点(key 是下级字符，values为子节点）
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        // 添加子节点
        public void addSubNode(Character c,TrieNode node){
            subNodes.put(c,node);
        }

        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }

    }

    // 将敏感词添加到前缀树上
    private void addKeyword(String keyword){

        TrieNode tempNode = rootNode;
        for(int i = 0;i < keyword.length();i++){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode == null){
                // 初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }

            // 指向子节点，进入下一轮循环
            tempNode = subNode;

            // 设置结束标识
            if(i == keyword.length() -1){
                tempNode.setKeywordEnd(true);
            }

        }

    }




    // 编写过滤敏感词的方法

    /**
     *
     * @param text 带过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text){
        // 为空输出null
        if(StringUtils.isEmpty(text)){
            return null;
        }

        // 指针1
        TrieNode tempNode = rootNode;
        // 指针2
        int begin = 0;
        // 指针3
        int position = 0;

        StringBuffer sb = new StringBuffer();

        while(position < text.length()){
            char c = text.charAt(position);

            // 跳过符号
            if (isSymbol(c)){
                // 若指针1处于根节点，将此符号计入结果，让指针2向下走一步
                if(tempNode == rootNode){
                    sb.append(c);
                    begin++;
                }
                //无论符号在开头还是在中间，指针3都向下走一步
                position++;
                continue;
            }

            // 检查下级节点
            tempNode = tempNode.getSubNode(c);
            if(tempNode == null){
                // 以begin开头的字符串不是敏感词
                sb.append(text.charAt(begin));
                // 进入下一个位置
                position = ++ begin;
                // 重新指向根节点
                tempNode = rootNode;
            }else if (tempNode.isKeywordEnd()){
                // 发现敏感词，将begin-position字符串对换
                sb.append(REPLACEMENT);
                // 进入下一个位置
                begin = ++position;
                // 重新指向根节点
                tempNode = rootNode;
            }else{
                // 检查下一个字符
                position++;

            }
        }

        // 将最后一批字符计入结果
        sb.append(text.substring(begin));

        return sb.toString();
    }

    // 判断是否为符号
    private boolean isSymbol(Character c){
//        (c<0x2E80 || c> 0x9FFF)在这之间是东亚的字符
        return !CharUtils.isAsciiAlphanumeric(c) && (c<0x2E80 || c> 0x9FFF);
    }
}
