/*
 * Copyright 2011- Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package spark.route;

import spark.Spark;
import spark.route.RouteMatcher.MatcherImplementation;

/**
 * RouteMatcherFactory
 *
 * @author Per Wendel
 */
public final class RouteMatcherFactory {
	/**
	 * The logger.
	 */
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
			.getLogger(RouteMatcherFactory.class);

	private static RouteMatcher routeMatcher = null;
	public static MatcherImplementation impl = MatcherImplementation.list;

	private RouteMatcherFactory() {
	}

	public static synchronized RouteMatcher get() {
		if (routeMatcher == null) {
			LOG.debug("creates RouteMatcher");
			switch (impl) {
				case list:
					routeMatcher = new SimpleRouteMatcher();
					break;
				case trie:
					routeMatcher = new TrieRouteMatcher();
					break;
				default:
					throw new IllegalArgumentException();
		}
		}
		return routeMatcher;
	}

}
