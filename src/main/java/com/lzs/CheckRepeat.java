package com.lzs;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class CheckRepeat {

  static String path = "";
  static Set<File> files = new HashSet<File>();

  public static void main(String[] args) throws Exception {
    if (path != null && !path.equals("")) {
      getFiles(path);
      System.out.println(files.size());
    } else {
      System.out.println("路径为空");
    }
  }

  /**
   * 递归获取某路径下的所有文件，文件夹，并输出
   */
  public static void getFiles(String path) {
    File file = new File(path);
    // 如果这个路径是文件夹
    if (file.isDirectory()) {
      // 获取路径下的所有文件
      File[] files = file.listFiles();
      for (int i = 0; i < files.length; i++) {
        // 如果还是文件夹 递归获取里面的文件 文件夹
        if (files[i].isDirectory()) {
          // System.out.println("目录：" + files[i].getPath());
          getFiles(files[i].getPath());
        } else {
          file = files[i];
          copyFile(file);
        }
      }
    } else {
      copyFile(file);
    }
  }

  private static void copyFile(File file) {
    String name = file.getName();
    for (File f : files) {
      if (f.getName().equals(name)) {
        System.out.println(file);
        System.out.println(f);
        System.out.println();
        break;
      }
    }
    files.add(file);
  }
}
