/***************************************************************
 *      _______  ______      ___       ______       __  ___    *
 *     /       ||   _  \    /   \     |   _  \     |  |/  /    *
 *    |   (----`|  |_)  |  /  ^  \    |  |_)  |    |  '  /     *
 *     \   \    |   ___/  /  /_\  \   |      /     |    <      *
 * .----)   |   |  |     /  _____  \  |  |\  \----.|  .  \     *
 * |_______/    | _|    /__/     \__\ | _| `._____||__|\__\    *  
 *                                                             *
 **************************************************************/
package spark.webserver;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import spark.WebContext;
import spark.route.HttpMethod;
import spark.route.RouteMatch;
import spark.route.RouteMatcher;
import spark.utils.SparkUtils;

/**
 * TODO: discover new TODOs.
 * 
 * TODO: There will be problems with annotation scanning when Spark is depended as a maven dependency...
 * TODO: Check if scannotation works better with maven/windows. If not, don't use it
 * 
 * TODO: Do we want get-prefixes for all *getters* or do we want a more ruby like approach??? (Maybe have two choices?)
 * 
 * TODO: Before annotion for filters...check sinatra page 
 * TODO: Setting Body, Status Code and Headers
 * TODO: Make available as maven dependency, upload on repo etc...
 * TODO: Add *, splat possibility
 * TODO: Add validation of routes, invalid characters and stuff, also validate parameters, check static, ONGOING
 * TODO: Add possibility to access HttpServletContext in method impl.
 * TODO: Javadoc
 * TODO: Add possibility to set content type on return
 * TODO: Create maven archetype, "ONGOING"
 * TODO: Add cache-control helpers
 * 
 * advanced TODO list:
 * TODO: sessions? (use session servlet context?)
 * TODO: Add regexp URIs 
 * 
 * Ongoing
 * TODO: Redirect func in web context, Partly DONE
 * TODO: Refactor, extract interfaces, ONGOING
 * 
 * TODO: Figure out a nice name, DONE - SPARK
 * TODO: Add /uri/{param} possibility, DONE
 * TODO: Tweak log4j config, DONE
 * TODO: Query string in web context, DONE
 * TODO: Add URI-param fetching from webcontext ie. ?param=value&param2=...etc, AND headers, DONE
 *
 * @author Per Wendel
 */
class MatcherFilter implements Filter {

    private RouteMatcher routeMatcher;
    
    /** The logger. */
    private static final Logger LOG = Logger.getLogger(MatcherFilter.class);
    
    public MatcherFilter(RouteMatcher routeMatcher) {
        this.routeMatcher = routeMatcher;
    }
    
    public void init(FilterConfig filterConfig) {}
    
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {
        long t0 = System.currentTimeMillis();
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String httpMethod = httpRequest.getMethod().toLowerCase();
        String uri = httpRequest.getRequestURI().toLowerCase();
        
        LOG.info("httpMethod:" + httpMethod + ", uri: " + uri);
        
        RouteMatch match = routeMatcher.findTargetForRequestedRoute(HttpMethod.valueOf(httpMethod), uri);

        Method target = null;
        
        WebContext webContext = null;
        if (match != null) {
            webContext = new WebContext(match, httpRequest, httpResponse);
            
            // TODO: Do something with the web context
            
            target = match.getTarget();
        }
        
        System.out.println("target: " + target);
        
        if (target != null) {
            try {
                Object result;
                if (SparkUtils.isStatic(target)) {
                    target.getParameterTypes();
                    if (SparkUtils.hasWebContext(target)) {
                        result = target.invoke(target.getDeclaringClass(), webContext);
                    } else {
                        result = target.invoke(target.getDeclaringClass());    
                    }
                    
                } else {
                    LOG.warn("Method: '" + target + "' should be static.");
                    httpResponse.sendError(500, "Target method not static");
                    return;
                }
                if (result != null) {
                    httpResponse.getOutputStream().write(result.toString().getBytes("utf-8"));
                }
                long t1 = System.currentTimeMillis() - t0;
                
                LOG.info("Time for request: " + t1);
            } catch (Exception e) {
                LOG.error(e);
            }    
        } else {
            httpResponse.sendError(404);    
        }
    }

    public void destroy() {
        // TODO Auto-generated method stub
    }
}