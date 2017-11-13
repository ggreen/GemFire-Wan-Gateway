package io.pivotal.client;

import java.net.InetSocketAddress;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.client.ClientCache;
import com.gemstone.gemfire.cache.client.ClientCacheFactory;
import com.gemstone.gemfire.cache.client.ClientRegionFactory;
import com.gemstone.gemfire.cache.client.ClientRegionShortcut;
import com.gemstone.gemfire.cache.client.PoolFactory;
import com.gemstone.gemfire.cache.client.PoolManager;
import com.gemstone.gemfire.pdx.ReflectionBasedAutoSerializer;

import io.pivotal.domain.Session;

public class SessionTests {

	private static ClientCache clientCache;
	private static String regionName = "test";

	/**
	 * Dynamically creates a connection to a cluster 
	 * @param host - the host locators (can be injected via properties)
	 * @param port - locator ports (can be injected)
	 */
	public static void CreateClientRegionWithPool(String host, int port) {
		String poolName = "pool";
		if (clientCache != null) {
			// if we're already set to the desired port, no need to switch the pool
			List<InetSocketAddress> locators = PoolManager.find(poolName).getLocators();
			if (locators.get(0).getPort() == port) {
				return;
			}
			clientCache.close();
		}
		
		ClientCacheFactory ccf = new ClientCacheFactory();

		ccf.setPdxSerializer(new ReflectionBasedAutoSerializer(".*"));
		ccf.setPdxReadSerialized(false);
		clientCache = ccf.create();
		
		ClientRegionFactory<?, ?> crf = clientCache.createClientRegionFactory(ClientRegionShortcut.PROXY);
		crf.setPoolName(poolName);
		PoolFactory pf = PoolManager.createFactory();
		pf.addLocator(host, port);
		pf.create(poolName);
		crf.create(regionName);
	}

	/*
	 * Tests a single put to the local cluster. Passes the App ID via argument
	 */
	@Test
	public void testOneSession() {
		CreateClientRegionWithPool("localhost", 10334);
		int counter = 0;
		String id = Integer.toString(counter);
		byte[] payload = new byte[7000];
		Session session = new Session(id, payload);

		Region<String, Session> sessionRegion = clientCache.getRegion(regionName);
		sessionRegion.put(id, session, "SES");
		Session Session2 = sessionRegion.get(id);
		Assert.assertTrue(Session2 != null);
	}

	/*
	 * Tests many puts to the local cluster. Passes the App ID via argument
	 */
	@Test
	public void testManySessions() {
		CreateClientRegionWithPool("localhost", 10334);
		Region<String, Session> sessionRegion = clientCache.getRegion(regionName);
		for (int counter = 1; counter < 1000; counter++) {
			String id = Integer.toString(counter);
			byte[] payload = new byte[7000];
			Session Session = new Session(id, payload);

			sessionRegion.put(id, Session, "SES");
		}
	}

	/*
	 * Tests a single put to the remote cluster. Passes the App ID via argument
	 */
	@Test
	public void testOneRemoteSession() {
		int counter = 1000;
		String id = Integer.toString(counter);
		byte[] payload = new byte[7000];
		Session session = new Session(id, payload);

		CreateClientRegionWithPool("localhost", 10335);
		Region<String, Session> sessionRegion = clientCache.getRegion(regionName);

		sessionRegion.put(id, session, "SES");
		Session Session2 = sessionRegion.get(id);
		Assert.assertTrue(Session2 != null);

	}

	/*
	 * Tests many puts to the remote cluster. Passes the App ID via argument
	 */
	@Test
	public void testManyRemoteSession() {
		CreateClientRegionWithPool("localhost", 10335);
		Region<String, Session> sessionRegion = clientCache.getRegion(regionName);
		for (int counter = 1001; counter < 2000; counter++) {
			String id = Integer.toString(counter);
			byte[] payload = new byte[7000];
			Session Session = new Session(id, payload);
			sessionRegion.put(id, Session, "SES");
		}
	}
}
