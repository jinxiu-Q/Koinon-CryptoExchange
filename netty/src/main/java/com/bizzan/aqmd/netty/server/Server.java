package com.bizzan.aqmd.netty.server;

/**
 * 
 * <p>Title: Server</p>
 * <p>Description: 服务端统一接口，定义作为服务器的通用方法</p>
 * @author Hevin QQ:390330302 E-mail:bizzanex@gmail.com
 * @date 2020年6月26日
 */
public interface Server {
	/**
	 * 
	 * <p>Title: open</p>
	 * <p>Description: </p>
	 * 启动服务，一般随spring启动加载具体的Server实例后执行
	 */
	public void  open();
	/**
	 * 
	 * <p>Title: close</p>
	 * <p>Description: </p>
	 * 断开管道并且退出。<br/>
	 * 当具体的Server实例销毁的时候需要调用该方法释放资源
	 */
	public void close();
	/**
	 * 
	 * <p>Title: isClosed</p>
	 * <p>Description: </p>
	 * 判断当前实例的管道是否已经关闭
	 * @return
	 */
	public boolean isClosed();
	/**
	 * 
	 * <p>Title: isAvailable</p>
	 * <p>Description: </p>
	 * 判断当前服务是否可用
	 * @return
	 */
	public boolean isAvailable();
}
