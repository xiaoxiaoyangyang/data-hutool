/**  
* <p>Title: SnowflakeIdWorker.java</p>  
* <p>Description: </p>  
* <p>Copyright: Copyright (c) 2019</p>    
* @author zhouyanqiu  
* @date 2019年4月17日  
* @version 1.0  
*/
package com.example.demo.util;


import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * @author zhouyanqiu
 *
 */
public class SnowflakeIdWorker {

	private final long twepoch = 1489111610226L;
	/** 机器id所占的位数 */
	private final long workerIdBits = 5L;
	/** 数据标识id所占的位数 */
	private final long dataCenterIdBits = 5L;
	/** 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数) */
	private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
	/** 支持的最大数据标识id，结果是31 */
	private final long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);
	/** 序列在id中占的位数 */
	private final long sequenceBits = 12L;
	/** 机器ID向左移12位 */
	private final long workerIdShift = sequenceBits;
	/** 数据标识id向左移17位(12+5) */
	private final long dataCenterIdShift = sequenceBits + workerIdBits;
	/** 时间截向左移22位(5+5+12) */
	private final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;
	/** 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095) */
	private final long sequenceMask = -1L ^ (-1L << sequenceBits);
	/** 工作机器ID(0~31) */
	private long workerId;
	/** 数据中心ID(0~31) */
	private long dataCenterId;
	/** 毫秒内序列(0~4095) */
	private long sequence = 0L;
	/** 上次生成ID的时间截 */
	private long lastTimestamp = -1L;
	private static SnowflakeIdWorker idWorker;
	static {
		idWorker = new SnowflakeIdWorker(getWorkId(), getDataCenterId());
	}

	/**
	 * <p>
	 * Title:
	 * </p>
	 * <p>
	 * Description:
	 * </p>
	 * 
	 * @param workId
	 * @param dataCenterId2
	 */

	public SnowflakeIdWorker(Long workId, Long dataCenterId2) {
		if (workerId > maxWorkerId || workerId < 0) {
			throw new IllegalArgumentException(
					String.format("workerId can't be greater than %d or less than 0", maxWorkerId));
		}
		if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
			throw new IllegalArgumentException(
					String.format("dataCenterId can't be greater than %d or less than 0", maxDataCenterId));
		}
		this.workerId = workerId;
		this.dataCenterId = dataCenterId;
	}

	// FIXME  static
	public synchronized long nextId() {
		long timestamp = timeGen();
		// 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
		if (timestamp < lastTimestamp) {
			throw new RuntimeException(String.format(
					"Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
		} // 如果是同一时间生成的，则进行毫秒内序列
		if (lastTimestamp == timestamp) {
			sequence = (sequence + 1) & sequenceMask;
			// 毫秒内序列溢出
			if (sequence == 0) {
				// 阻塞到下一个毫秒,获得新的时间戳
				timestamp = tilNextMillis(lastTimestamp);
			}
		}

		// 时间戳改变，毫秒内序列重置
		else {
			sequence = 0L;
		}
		// 上次生成ID的时间截
		lastTimestamp = timestamp;
		// 移位并通过或运算拼到一起组成64位的ID
		return ((timestamp - twepoch) << timestampLeftShift) | (dataCenterId << dataCenterIdShift)
				| (workerId << workerIdShift) | sequence;
	}

	/**
	 * * 阻塞到下一个毫秒，直到获得新的时间戳 *
	 * 
	 * @param lastTimestamp 上次生成ID的时间截 *
	 * @return 当前时间戳
	 */
	protected long tilNextMillis(long lastTimestamp) {
		long timestamp = timeGen();
		while (timestamp <= lastTimestamp) {
			timestamp = timeGen();
		}
		return timestamp;
	}

	/**
	 * 
	 * 返回以毫秒为单位的当前时间
	 * 
	 * @return 当前时间(毫秒)
	 * 
	 */

	protected long timeGen() {

		return System.currentTimeMillis();

	}

	private static Long getWorkId() {
		try {
			String hostAddress = Inet4Address.getLocalHost().getHostAddress();
			int[] ints = StringUtils.toCodePoints(hostAddress);
			int sums = 0;
			for (int b : ints) {
				sums += b;
			}
			return (long) (sums % 32);
		} catch (UnknownHostException e) { // 如果获取失败，则使用随机数备用 return RandomUtils.nextLong(0,31); } }
			return RandomUtils.nextLong(0, 31);
		}
	}

	private static Long getDataCenterId() {
		int[] ints = StringUtils.toCodePoints(SystemUtils.getHostName());
		int sums = 0;
		for (int i : ints) {
			sums += i;
		}
		return (long) (sums % 32);
	}

//	public static void main(String[] args) {
//		System.out.println(System.currentTimeMillis());
//		long startTime = System.nanoTime();
//		for (int i = 0; i < 50000; i++) {
//			long id = SnowflakeIdWorker.generateId();
//			System.out.println(id);
//		}
//		System.out.println((System.nanoTime() - startTime) / 1000000 + "ms");
//	}

	/** * 静态工具类 * * @return */
	public static Long generateId() {
		long id = idWorker.nextId();
		return id;
	}

}