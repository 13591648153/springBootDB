/**
 * @(#)DbUtils.java 2018年12月20日
 * Copyright 2018 Neusoft Group Ltd. All rights reserved.
 * Neusoft PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *******************************************************************************/
package com.yd.test.components.mp.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yd.test.query.Condition;
import com.yd.test.query.Criteria;
import com.yd.test.query.Pagination;
import com.yd.test.result.PageResponseBody;

/**
 * @author <a href="mailto:li-chp@neusoft.com"> li-chp </a>
 * @version $Revision 1.0.0 $ 2018年12月20日 上午9:23:27
 */
public class MpUtils<T> {
    //test
	private static String[] oneParamMethods = {"isNull", "isNotNull"};
	private static String[] oneDynamicParamMethods = {"orderByAsc", "orderByDesc"};
	private static String[] twoParamMethods = {"eq", "ne", "like", "notLike", "likeLeft", "likeRight", "ge", "gt", "le", "lt"};
	private static String[] twoDynamicParamMethods = {"in", "notIn"};
	private static String[] threeParamMethods = {"between", "notBetween"};
	private static final List<String> oneParamMethodList = Arrays.asList(oneParamMethods);
	private static final List<String> oneDynamicParamMethodList = Arrays.asList(oneDynamicParamMethods);
	private static final List<String> twoParamMethodList = Arrays.asList(twoParamMethods);
	private static final List<String> twoDynamicParamMethodList = Arrays.asList(twoDynamicParamMethods);
	private static final List<String> threeParamMethodsList = Arrays.asList(threeParamMethods);

	/**
	 * 查询分页封装
	 * @author 何老师
	 * @param baseMapperT
	 * @param criteria
	 * @return
	 * @throws Exception
	 */
	public PageResponseBody<T> selectPage(BaseMapper<T> baseMapperT, Criteria criteria) throws Exception {

		// 如果页面不传入分页信息，默认分1页，每页1000条
		if (criteria.getPagination() == null) {
			Pagination pagination = new Pagination();
			pagination.setCurrent(1L);
			pagination.setSize(1000L);
			criteria.setPagination(pagination);
		} else {
			if (criteria.getPagination().getSize() == null || criteria.getPagination().getSize() == 0L) {
				criteria.getPagination().setSize(1000L);
			}
			if (criteria.getPagination().getCurrent() == null || criteria.getPagination().getCurrent() == 0L) {
				criteria.getPagination().setCurrent(1L);
			}
		}

		QueryWrapper<T> queryWrapper = makeQueryWrapper(criteria);
		Integer count = baseMapperT.selectCount(queryWrapper);
		Pagination pagination = criteria.getPagination();
		IPage<T> page = new Page<T>(pagination.getCurrent(), pagination.getSize(), count);
		IPage<T> iPage = baseMapperT.selectPage(page, queryWrapper);

		PageResponseBody<T> pageResponseBody = new PageResponseBody<T>();
		BeanUtils.copyProperties(iPage, pageResponseBody);
		return pageResponseBody;
	}

	/**
	 * 生成查询条件
	 * @param criteria
	 * @return ModelT
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static <T> QueryWrapper<T> makeQueryWrapper(Criteria criteria) throws Exception {
		QueryWrapper<T> qw = new QueryWrapper<T>();
		List<Condition> conditons = criteria.getConditons();

		if (CollectionUtils.isNotEmpty(conditons)) {
			for (Condition c : conditons) {
				String op = c.getOpEnum().getOp();
				for (Entry<String, Object> entry : c.getKwargs().entrySet()) {
					if (oneParamMethodList.contains(op)) {
						Method method = qw.getClass().getMethod(op, Object.class);
						method.invoke(qw, underline(entry.getKey()));
					} else if (oneDynamicParamMethodList.contains(op)) {
						Method method = qw.getClass().getMethod(op, Object[].class);
						method.invoke(qw, new Object[] {underline(entry.getKey()).split(",")});
					} else if (twoParamMethodList.contains(op)){
						Method method = qw.getClass().getMethod(op, Object.class, Object.class);
						method.invoke(qw, underline(entry.getKey()), entry.getValue());
					} else if (twoDynamicParamMethodList.contains(op)) {
						Method method = qw.getClass().getMethod(op, Object.class, Collection.class);
						method.invoke(qw, underline(entry.getKey()), (ArrayList<Object>) entry.getValue());
					} else if (threeParamMethodsList.contains(op)) {
						Method method = qw.getClass().getMethod(op, Object.class, Object.class, Object.class);
						List<Object> list = (ArrayList<Object>) entry.getValue();
						method.invoke(qw, underline(entry.getKey()), list.get(0), list.get(1));
					}
				}
			}
		}
		return qw;
	}

	/**
	 * 驼峰转下划线
	 * @param str
	 * @return
	 */
	public static String underline(String str) {
		Pattern pattern = Pattern.compile("[A-Z]");
		Matcher matcher = pattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
		matcher.appendTail(sb);
		return sb.toString();
	}

}
