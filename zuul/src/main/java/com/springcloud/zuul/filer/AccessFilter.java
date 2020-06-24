package com.springcloud.zuul.filer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;

@Component
public class AccessFilter extends ZuulFilter {
	// 统计当前Zuul调用次数
	int count = 0;

	// 获取Zuul服务端口号
	@Value("${server.port}")
	private String prot;

	/**
	 * 判断过滤器是否执行,直接返回true,代表对所有请求过滤 此方法指定过滤范围
	 */
	@Override
	public boolean shouldFilter() {
		return true;
	}

	/**
	 * 过滤的具体逻辑
	 */
	@Override
	public Object run() throws ZuulException {
//		// 1）获取zuul提供的请求上下文对象（即是请求全部内容）
//		RequestContext currentContext = RequestContext.getCurrentContext();
//		// 2) 从上下文中获取request对象
//		HttpServletRequest request = currentContext.getRequest();
//		// 3) 从请求中获取token
//		String token = request.getParameter("access-token");
//		// 4) 判断（如果没有token，认为用户还没有登录，返回401状态码）
//		if (token == null || "".equals(token.trim())) {
//			// 没有token，认为登录校验失败，进行拦截
//			currentContext.setSendZuulResponse(false);
//			// 返回401状态码。也可以考虑重定向到登录页
//			currentContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
//		}
		// 否则正常执行业务逻辑，调用服务.....
		System.out.println("访问Zuul网关端口为：" + prot + "（total：" + (count++) + "）");
		return null;
	}

	/**
	 * 过滤器的类型,它决定过滤器在请求的哪个生命周期中执行<br>
	 * pre : 请求被路由之前做一些前置工作 ,比如请求和校验<br>
	 * routing : 在路由请求时被调用,路由请求转发,即是将请求转发到具体的服务实例上去.<br>
	 * post : 在routing和error过滤器之后被调用..所以post类型的过滤器可以对请求结果进行一些加工<br>
	 * error : 处理请求发生错误时调用
	 */
	@Override
	public String filterType() {
		return "pre";
	}

	/**
	 * 过滤器的执行顺序. 在一个阶段有多个过滤器时,需要用此指定过滤顺序 数值越小优先级越高
	 */
	@Override
	public int filterOrder() {
		return 0;
	}

}
