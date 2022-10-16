package com.lzs;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

public class Rename {

  static int totalChangeNum = 0;
  static int totalDeleteNum = 0;
  static int totalNothingNum = 0;
  static String path = "";

  public static void main(String[] args) throws Exception {
    if (path != null && !path.equals("")) {
      getFiles(path);
      System.out.println("总修改文件数：" + totalChangeNum);
      System.out.println("总删除文件数：" + totalDeleteNum);
      System.out.println("不做处理文件数：" + totalNothingNum);
    } else {
      System.out.println("路径为空");
    }
  }

  private static Date getChangeTime(File file) {
    // 先尝试获取Exif的Original时间
    try {
      Metadata metadata = JpegMetadataReader.readMetadata(file);
      Iterator<Directory> it = metadata.getDirectories().iterator();
      while (it.hasNext()) {
        Directory exif = it.next();
        Iterator<Tag> tags = exif.getTags().iterator();
        while (tags.hasNext()) {
          Tag tag = (Tag) tags.next();
          if (tag.getTagName().equals("Date/Time Original")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
            return sdf.parse(tag.getDescription());
          }
        }
      }
    } catch (Exception e) {}
    // 再获取最后一次修改时间
    Path path = Paths.get(file.getPath());
    BasicFileAttributeView basicview = Files.getFileAttributeView(
      path,
      BasicFileAttributeView.class,
      LinkOption.NOFOLLOW_LINKS
    );
    BasicFileAttributes attr;
    try {
      attr = basicview.readAttributes();
      Date createDate = new Date(attr.lastModifiedTime().toMillis());
      return createDate;
    } catch (Exception e) {
      e.printStackTrace();
    }
    Calendar cal = Calendar.getInstance();
    cal.set(1970, 0, 1, 0, 0, 0);
    return cal.getTime();
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
    String newName = new SimpleDateFormat("yyyyMMddHHmmss")
    .format(getChangeTime(file));
    System.out.println(file);
    File newFile = new File(
      file.getParent() +
      File.separator +
      newName +
      file.getName().substring(file.getName().lastIndexOf("."))
    );
    System.out.println(newFile);

    if (file.toString().equals(newFile.toString())) {
      System.out.println("文件不作处理");
      totalNothingNum++;
    } else {
      if (!file.renameTo(newFile)) {
        System.out.println("修改文件已存在，删除原文件");
        file.delete();
        totalDeleteNum++;
      } else {
        totalChangeNum++;
      }
    }

    System.out.println();
  }
}
