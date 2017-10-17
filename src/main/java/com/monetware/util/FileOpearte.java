package com.monetware.util;

import com.google.gson.JsonObject;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;



public class FileOpearte {
	// 在文末换行，并添加文本内容
		public static void writeJsonLine(InputStream is, String goal) {
			try {
				StringBuffer sb = new StringBuffer("");
				BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8")); 
				String str = null;
				JsonObject jsonObject=new JsonObject();
				long lineNo=0;
				while ((str = br.readLine()) != null) {
					lineNo++;
					str.replaceAll("\r\n", "#[rn]");
					str.replaceAll("\"", "“");
					jsonObject.addProperty("content", str);
					sb.append(jsonObject.toString()+"\r\n");
					//1000行处理一次
					if (lineNo%1000==0) {
						System.out.println("=========>"+new String(sb.toString().getBytes(),"UTF-8"));
						writeOneLine(new String(sb.toString().getBytes(),"UTF-8"),goal);
						sb.setLength(0);
					}
				}
				if (sb.length()!=0) {
					writeOneLine(new String(sb.toString().getBytes(),"UTF-8"),goal);
				}
				br.close();
				is.close();
			} catch (Exception e) {
				System.out.println(e);
			}

		}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// 在文末换行，并添加文本内容
	public static void addFileContent(InputStream is, String goal) {
		try {
			StringBuffer sb = new StringBuffer("");
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8")); 
			String str = null;
			long lineNo=0;
			while ((str = br.readLine()) != null) {
				lineNo++;
				sb.append("\r\n"+str);
				//1000行处理一次
				if (lineNo%1000==0) {
					System.out.println("=========>"+new String(sb.toString().getBytes(),"UTF-8"));
					writeOneLine(new String(sb.toString().getBytes(),"UTF-8"),goal);
					sb.setLength(0);
				}
			}
			System.out.println("=========>"+new String(sb.toString().getBytes(),"UTF-8"));
			writeOneLine(new String(sb.toString().getBytes(),"UTF-8"),goal);
			br.close();
			is.close();
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	// 获取文件内容行数
	public static long getLineNo(String path) {
		try {
			// read file content from file
			File file = new File(path);
			long lineNo = 0;
			if (!file.exists()) {
				try {
					file.createNewFile();
					return lineNo;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			FileReader reader = new FileReader(path);
			BufferedReader br = new BufferedReader(reader);
			while ((br.readLine()) != null) {
				lineNo = lineNo + 1;
			}
			br.close();
			reader.close();

			return lineNo;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;

	}


	

	// 在文件末尾新增一行内容
	public static void writeOneLine(String append,String path) {
		FileChannel channel = null;
		FileLock lock = null;
		File file = new File(path);
		try {

			if (!file.exists()) {

				file.createNewFile();

			}

			RandomAccessFile rf = new RandomAccessFile(path, "rw");

//			channel = rf.getChannel();
			// 文件进程锁，当文件锁不可用时，当前进程会被挂起
			// 无参lock()默认为独占锁，不会报NonReadableChannelException异常，因为独占就是为了写
//			lock = channel.lock();

			// 将指针移动到文件末尾
			rf.seek(rf.length());
			// 设置编码
//			append = new String(append.getBytes("utf-8"), "utf-8");
			System.out.println(append);
			rf.writeBytes(append);
			// 释放锁
//			lock.release();
			lock = null;
//			channel.close();
			channel = null;
			rf.close();// 关闭文件流
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (lock != null) {
				try {
					lock.release();
					lock = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (channel != null) {
				try {
					channel.close();
					channel = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}


	/**
	 *
	 *拷贝文件
	 * @param source 源文件
	 * @param goal 目标路径
	 */

	public static void fileChannelCopy(String source, String goal) {
		File s = new File(source);
		File t = new File(goal);
		if (!s.exists()) {
			return;
		}
		if (!t.getParentFile().exists()) {
			// 如果目标文件所在的目录不存在，则创建父目录
			t.getParentFile().mkdirs();

		}

		FileInputStream fi = null;

		FileOutputStream fo = null;

		FileChannel in = null;

		FileChannel out = null;

		try {

			fi = new FileInputStream(s);

			fo = new FileOutputStream(t);

			in = fi.getChannel();// 得到对应的文件通道

			out = fo.getChannel();// 得到对应的文件通道

			in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				fi.close();

				in.close();

				fo.close();

				out.close();

			} catch (IOException e) {

				e.printStackTrace();

			}

		}

	}

	// 递归算法拷贝文件夹
	public static void copy(String source, String goal) {

		File[] fl = new File(source).listFiles();
		File file = new File(goal);
		if (!file.exists()) { // 如果文件夹不存在
			file.mkdir();
		} // 建立新的文件夹
		for (int i = 0; i < fl.length; i++) {
			if (fl[i].isFile()) { // 如果是文件类型就复制文件
				try {
					File file2 = new File(file.getPath() + File.separator + fl[i].getName());
					if (!file2.getParentFile().exists()) {
						// 如果目标文件所在的目录不存在，则创建父目录
						file2.getParentFile().mkdirs();
					}
					FileInputStream fis = new FileInputStream(fl[i]);
					FileOutputStream out = new FileOutputStream(
							new File(file.getPath() + File.separator + fl[i].getName()));
					int count = fis.available();
					byte[] data = new byte[count];
					if ((fis.read(data)) != -1) {
						out.write(data); // 复制文件内容
					}
					out.close(); // 关闭输出流
					fis.close(); // 关闭输入流
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (fl[i].isDirectory()) { // 如果是文件夹类型
				File des = new File(file.getPath() + File.separator + fl[i].getName());
				des.mkdir(); // 在目标文件夹中创建相同的文件夹
				copy(fl[i].getAbsolutePath(), des.getAbsolutePath()); // 递归调用方法本身
			}
		}
	}

	// 更新文件名
	public static void updateFileName(String rootPath, String oldName, String newName) {
		try {
			File file = new File(rootPath + oldName); // 指定文件名及路径
			file.renameTo(new File(rootPath + newName)); // 改名
		} catch (Exception e) {
			System.out.println(e);
			return;
			// TODO: handle exception
		}
	}




//写入最后一行
public static void writeEndline(String filepath, String string)
		throws Exception {

	RandomAccessFile file = new RandomAccessFile(filepath, "rw");
	long len = file.length();
	long start = file.getFilePointer();
	long nextend = start + len - 1;
	byte[] buf = new byte[1];
	file.seek(nextend);
	file.read(buf, 0, 1);

	if (buf[0] == '\n')

		file.writeBytes(string);
	else

		file.writeBytes("\r\n"+string);

	file.close();

}
//覆盖最后一行
	public static void reWriteEndline(String filepath, String string)
			throws Exception {

		RandomAccessFile file = new RandomAccessFile(filepath, "rw");
		long len = file.length();
		long start = file.getFilePointer();
		long nextend = start + len - 1;

		int i = -1;
		file.seek(nextend);
		byte[] buf = new byte[1];

		while (nextend > start) {

			i = file.read(buf, 0, 1);
			if (buf[0] == '\r') {
				file.setLength(nextend - start);
				break;
			}
			nextend--;
			file.seek(nextend);

		}
		file.close();
		writeEndline(filepath, string);

	}






}