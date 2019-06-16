package com.audio.main;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.audio.AudioStreamSequence;

public class AudioHelper {
	/**
	 * 测试读取数据库中所有的记录
	 */
	public static void test() {
		String url = "jdbc:Access:///data//cantoneseDictionary.MDB";
		// URL格式：jdbc:Access:/// + "绝对路径或相对路径"

		Connection conn = null;
		try {
			Class.forName("com.hxtt.sql.access.AccessDriver");
			// 类名必须确保正确，同时需要导入Access_JDBC30.jar到build path
			conn = DriverManager.getConnection(url, "root", "root");
			Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

			// 分页查询
			int page = 0;
			boolean flag = false;
			while (!flag) {
				int start = page * 1000 + 1;
				int end = (page + 1) * 1000;
				String sql = "select * from cantonesePhonetic where ID between " + start + " and " + end;
				ResultSet rs = statement.executeQuery(sql);

				if (SqlHelper.getResultSetSize(rs) < 1000) {
					flag = true;
				}

				while (rs.next()) {
					System.out.println(rs.getString(1) + rs.getString(2) + rs.getString(3));
				}

				page++;
			}
		} catch (

		Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 多个wav语音文件拼接
	 * 
	 * @param descFile
	 *            目标文件
	 * @param srcFile
	 *            源文件列表
	 * @throws IOException
	 * @throws UnsupportedAudioFileException
	 */
	public static void merge(String descFile, String... srcFile) throws IOException, UnsupportedAudioFileException {
		// 读取第一个源文件
		File firstFile = new File(srcFile[0]);
		AudioFileFormat firstFileFormat = AudioSystem.getAudioFileFormat(firstFile);
		AudioFormat firstFormat = AudioSystem.getAudioFileFormat(firstFile).getFormat();

		// 初始化数据帧长度
		int frameLength = 0;

		// 初始化容器
		Vector<AudioInputStream> vec = new Vector<AudioInputStream>();

		if (srcFile.length > 1) {
			for (int i = 0; i < srcFile.length; i++) {
				try {
					// 读取下一个文件
					FileInputStream nextFile = new FileInputStream(srcFile[i]);

					// java.io.IOException: mark/reset not supported
					// 解决方案：给定的流不支持mark和reset就会报这个错误,解决方案是用BufferedInputStream把原来的流包一层.
					AudioInputStream next_ais = AudioSystem.getAudioInputStream(new BufferedInputStream(nextFile));

					// 计算目前的文件长度，同时将当前文件加入到容器中
					frameLength += next_ais.getFrameLength();
					vec.add(next_ais);
				} catch (Exception e) {
					System.out.println(srcFile[i] + "音频文件不存在");
				}
			}

			// 整合多个流
			AudioStreamSequence sis = new AudioStreamSequence(vec.elements());

			// 写入文件中
			AudioInputStream out = new AudioInputStream(sis, firstFormat, frameLength);

			AudioSystem.write(out, firstFileFormat.getType(), new File(descFile));

			// 关闭文件
			if (sis != null)
				sis.close();
			for (AudioInputStream stream : vec) {
				stream.close();
			}
		}
	}

	/**
	 * 将输入的字符数组转换为粤语，并返回各个粤语读音的路径
	 * 
	 * @param charactors
	 * @return
	 */
	public static String translate(String charactors) {
		String url = "jdbc:Access:///data//cantoneseDictionary.MDB";
		// URL格式：jdbc:Access:/// + "绝对路径或相对路径"

		StringBuilder results = new StringBuilder();

		Connection conn = null;

		try {
			Class.forName("com.hxtt.sql.access.AccessDriver");
			// 类名必须确保正确，同时需要导入Access_JDBC30.jar到build path
			conn = DriverManager.getConnection(url, "", "");
			Statement statement = conn.createStatement();

			// 分页查询
			for (int i = 0; i < charactors.length(); i++) {
				String sql = "select * from cantonesePhonetic where Charactor in('" + charactors.charAt(i) + "')";
				ResultSet rs = statement.executeQuery(sql);

				while (rs.next()) {
					System.out.println(rs.getString(1) + rs.getString(2) + rs.getString(3));

					String audioPath = "sound//" + rs.getString(3) + ".wav";
					results.append(audioPath + ",");

					try {
						AudioStream stream = new AudioStream(new FileInputStream(audioPath));
						AudioPlayer.player.start(stream);
						Thread.sleep(1250);
					} catch (Exception e) {
						// TODO: handle exception
					}

					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return results.toString();
	}
}