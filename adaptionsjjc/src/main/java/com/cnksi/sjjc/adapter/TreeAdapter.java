package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 树形结构baseAdapter
 *
 * @author luoxy
 * @date 2016/5/26
 * @copyRight
 */
public abstract class TreeAdapter extends BaseAdapter<TreeNode> {

	private SparseArray<Integer> layoutMap;

	private TreeNode expandNode;

	private boolean showOnly = true;

	/**
	 * 是否只展示一个节点
	 *
	 * @param showOnly
	 */
	public void setShowOnly(boolean showOnly) {
		this.showOnly = showOnly;
	}

	/**
	 * @param context
	 * @param data
	 */
	public TreeAdapter(Context context, List<TreeNode> data) {
		super(context, data);
	}

	/**
	 * 设置每层布局文件
	 *
	 * @param layoutMap
	 */
	public TreeAdapter setLayoutMap(SparseArray<Integer> layoutMap) {
		this.layoutMap = layoutMap;
		return this;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		TreeNode treeNode = (TreeNode) getItem(position);
		if (null != convertView) {
			holder = (ViewHolder) convertView.getTag();
			int holderLevel = (Integer) holder.getTag();
			if (treeNode.level != holderLevel) {
				convertView = null;
				int layoutId = layoutMap.get(treeNode.level);
				holder = ViewHolder.get(context, convertView, parent, layoutId, position);
			}
		} else {
			int layoutId = layoutMap.get(treeNode.level);
			holder = ViewHolder.get(context, convertView, parent, layoutId, position);
		}
		holder.setTag(treeNode.level);
		convert(holder, treeNode, position);
		return holder.getRootView();
	}

	/**
	 * 展开关闭
	 *
	 * @param position
	 * @param treeNode
	 * @param expandAll
	 */
	public void expandPosition(int position, TreeNode treeNode, boolean expandAll) {
		// 节点为空或者没有子节点无响应
		if (null == treeNode || !treeNode.isParent() || !treeNode.isExpandedAble) {
			return;
		} else {
			// 节点展开,则节点关闭
			if (treeNode.isExpanded) {
				treeNode.isExpanded = false;
				List<TreeNode> delNodes = new ArrayList<TreeNode>();
				findChildTreeNode(treeNode, delNodes);
				data.removeAll(delNodes);
				this.notifyDataSetInvalidated();
			}
			// 节点关闭,则节点展开
			else {
				treeNode.isExpanded = true;
				List<TreeNode> expandNodes;
				if (expandAll) {
					expandNodes = new ArrayList<TreeNode>();
					findChildTreeNode(treeNode, expandNodes);
				} else {
					expandNodes = treeNode.childTreeNodes;
				}
				List<TreeNode> dataConnection = new ArrayList<TreeNode>(this.data);
				int expandSize = expandNodes.size();
				for (int i = 0; i < expandSize; i++) {
					TreeNode node = expandNodes.get(i);
					if (node.isParent() && expandAll) {
                        node.isExpanded = true;
                    }
					dataConnection.add(position + 1 + i, node);
				}
				// 只展示一个
				if (showOnly) {
					if (expandNode != null && !treeNode.equals(expandNode)) {
						List<TreeNode> delNodes = new ArrayList<TreeNode>();
						findChildTreeNode(expandNode, delNodes);
						expandNode.isExpanded = false;
						dataConnection.removeAll(delNodes);
					}
				}
				this.expandNode = treeNode;
				this.data.clear();
				this.data.addAll(dataConnection);
				this.notifyDataSetChanged();
			}
		}
	}

	public void explandAll() {
		for (int i = 0; i < data.size(); i++) {
			expandPosition(i, (TreeNode) getItem(i), true);
		}
	}

	/**
	 * 递归查询树形结构的下层节点
	 *
	 * @param rootNode
	 * @param childNode
	 */
	private void findChildTreeNode(TreeNode rootNode, List<TreeNode> childNode) {
		if (null != rootNode && null != childNode && rootNode.isParent()) {
			List<TreeNode> childList = rootNode.childTreeNodes;
			childNode.addAll(childList);
			for (TreeNode treeNode : childList) {
				findChildTreeNode(treeNode, childNode);
			}
		}
	}

}
