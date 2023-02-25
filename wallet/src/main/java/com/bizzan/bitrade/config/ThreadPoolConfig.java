package ai.turbochain.ipex.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 线程池
 * 
 * @author fly
 *
 */
@Configuration
public class ThreadPoolConfig {

	@Bean
	public ExecutorService getThreadPool() {
		// 创建一个可缓存线程池，如果线程池长度超过处理需要，可灵活回收空闲线程，若无可回收，则新建线程
		// return Executors.newCachedThreadPool();
		return Executors.newFixedThreadPool(20);
	}

}
