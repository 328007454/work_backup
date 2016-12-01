package com.cnksi.sjjc.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 树形结构
 *
 * @author luoxy
 * @date 2016/5/26
 * @copyRight
 */
public class TreeNode {
	/**
	 * 节点ID
	 */
	public String id = UUID.randomUUID().toString();

	/**
	 * 父节点
	 */
	public TreeNode parentNode;

	/**
	 * 节点在结构中的级别：默认0
	 */
	public int level;

	/**
	 * 节点是否展开
	 */
	public boolean isExpanded;
	/**
	 * 是否允许展开
	 */
	public boolean isExpandedAble = true;

	/**
	 * 节点绑定对象
	 */
	public Object bindObject;

	/**
	 * 子节点
	 */
	public List<TreeNode> childTreeNodes = new ArrayList<TreeNode>();

	public TreeNode() {
		this(null, true);
	}

	/**
	 * @param parentNode
	 * @param isExpandedAble
	 */
	public TreeNode(TreeNode parentNode, boolean isExpandedAble) {
		this.parentNode = parentNode;
		this.isExpandedAble = isExpandedAble;
	}

	/**
	 * @param parentNode
	 * @param level
	 * @param bindObject
	 */
	public TreeNode(TreeNode parentNode, int level, boolean isExpandedAble, Object bindObject) {
		this.parentNode = parentNode;
		this.level = level;
		this.isExpandedAble = isExpandedAble;
		this.bindObject = bindObject;
	}

	/**
	 * 添加子节点
	 *
	 * @param treeNode
	 */
	public void addChildNode(TreeNode treeNode) {
		childTreeNodes.add(treeNode);
	}

	/**
	 * 判断是否是子节点
	 *
	 * @return
	 */
	public boolean isChild() {
		return null == parentNode;
	}

	/**
	 * 判断是否是父节点
	 *
	 * @return
	 */
	public boolean isParent() {
		return !childTreeNodes.isEmpty();
	}

}
