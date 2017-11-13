package com.monetware.model.collect;

import java.io.File;

public class test {
    public static void main(String[] args)
    {
        File direc=new File("F:\\datas");
        if(direc.isDirectory())
        {
            File[] files=direc.listFiles();
            for(File file:files)
            {
                System.out.println(file.getName().replace(".txt",""));
            }
        }
    }
}
