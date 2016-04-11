package com.jiuqi.deploy.db;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import com.jiuqi.deploy.util.Check;

public class OracleTablespaceProperties implements TablespaceProperties {

	private static final long serialVersionUID = -6976279893674797115L;

	private String tablespaceName;

	private String dataFileName;

	private String dataFileDirectory;

	private String fileSize;

	private boolean autoExtend;

	private String autoExtendSize;

	private boolean unlimited;

	private String autoExtendMaxSize;

	///
	private String minimumExtentSize;

	private String maxExtents;
	private String initial;

	private String next;

	private String minExtents;


	private String pctIncrease;

	private boolean logging;

	private boolean offline;

	private boolean temporary;

	private boolean autoSegmentSpaceManagement;

	private boolean reuse;

	public String getTablespaceName() {
		return tablespaceName;
	}

	public void setTablespaceName(String tablespaceName) {
		this.tablespaceName = tablespaceName;
	}

	/**
	 * @return dataFile
	 */
	public String getDataFileName() {
		return dataFileName;
	}

	/**
	 * @param dataFile
	 *            dataFile
	 */
	public void setDataFileName(String dataFile) {
		this.dataFileName = dataFile;
	}

	public String getDataFileDirectory() {
		return dataFileDirectory;
	}

	public void setDataFileDirectory(String dataFileDirectory) {
		this.dataFileDirectory = dataFileDirectory;
	}

	/**
	 * @return fileSize
	 */
	public String getFileSize() {
		return fileSize;
	}

	/**
	 * @param fileSize
	 *            fileSize
	 */
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * autoExtend を取得します.
	 * 
	 * @return autoExtend
	 */
	public boolean isAutoExtend() {
		return autoExtend;
	}

	/**
	 * @param autoExtend
	 *            autoExtend
	 */
	public void setAutoExtend(boolean autoExtend) {
		this.autoExtend = autoExtend;
	}

	/**
	 * @return autoExtendSize
	 */
	public String getAutoExtendSize() {
		return autoExtendSize;
	}

	/**
	 * autoExtendSize を設定します.
	 * 
	 * @param autoExtendSize
	 *            autoExtendSize
	 */
	public void setAutoExtendSize(String autoExtendSize) {
		this.autoExtendSize = autoExtendSize;
	}

	public void setUnlimited(boolean unlimited) {
		this.unlimited = unlimited;
	}

	public boolean isUnlimited() {
		return unlimited;
	}

	/**
	 * 
	 * @return autoExtendMaxSize
	 */
	public String getAutoExtendMaxSize() {
		return autoExtendMaxSize;
	}

	/**
	 * 
	 * @param autoExtendMaxSize
	 *            autoExtendMaxSize
	 */
	public void setAutoExtendMaxSize(String autoExtendMaxSize) {
		this.autoExtendMaxSize = autoExtendMaxSize;
	}

	/**
	 * @return minimumExtentSize
	 */
	public String getMinimumExtentSize() {
		return minimumExtentSize;
	}

	/**
	 * @param minimumExtentSize
	 *            minimumExtentSize
	 */
	public void setMinimumExtentSize(String minimumExtentSize) {
		this.minimumExtentSize = minimumExtentSize;
	}

	/**
	 * logging を取得します.
	 * 
	 * @return logging
	 */
	public boolean isLogging() {
		return logging;
	}

	/**
	 * logging を設定します.
	 * 
	 * @param logging
	 *            logging
	 */
	public void setLogging(boolean logging) {
		this.logging = logging;
	}

	/**
	 * offline を取得します.
	 * 
	 * @return offline
	 */
	public boolean isOffline() {
		return offline;
	}

	/**
	 * @param offline
	 *            offline
	 */
	public void setOffline(boolean offline) {
		this.offline = offline;
	}

	/**
	 * @return temporary
	 */
	public boolean isTemporary() {
		return temporary;
	}

	/**
	 * @param temporary
	 *            temporary
	 */
	public void setTemporary(boolean temporary) {
		this.temporary = temporary;
	}

	/**
	 * @return autoSegmentSpaceManagement
	 */
	public boolean isAutoSegmentSpaceManagement() {
		return autoSegmentSpaceManagement;
	}

	/**
	 * @param autoSegmentSpaceManagement
	 *            autoSegmentSpaceManagement
	 */
	public void setAutoSegmentSpaceManagement(boolean autoSegmentSpaceManagement) {
		this.autoSegmentSpaceManagement = autoSegmentSpaceManagement;
	}

	/**
	 * @return initial
	 */
	public String getInitial() {
		return initial;
	}

	/**
	 * @param initial
	 *            initial
	 */
	public void setInitial(String initial) {
		this.initial = initial;
	}

	/**
	 * @return next
	 */
	public String getNext() {
		return next;
	}

	/**
	 * @param next
	 *            next
	 */
	public void setNext(String next) {
		this.next = next;
	}

	/**
	 * @return minExtents
	 */
	public String getMinExtents() {
		return minExtents;
	}

	/**
	 * @param minExtents
	 *            minExtents
	 */
	public void setMinExtents(String minExtents) {
		this.minExtents = minExtents;
	}

	/**
	 * @return maxExtents
	 */
	public String getMaxExtents() {
		return maxExtents;
	}

	/**
	 * @param maxExtents
	 *            maxExtents
	 */
	public void setMaxExtents(String maxExtents) {
		this.maxExtents = maxExtents;
	}

	/**
	 * @return pctIncrease
	 */
	public String getPctIncrease() {
		return pctIncrease;
	}

	/**
	 * @param pctIncrease
	 *            pctIncrease
	 */
	public void setPctIncrease(String pctIncrease) {
		this.pctIncrease = pctIncrease;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TablespaceProperties clone() {
		OracleTablespaceProperties properties = new OracleTablespaceProperties();

		properties.autoExtend = this.autoExtend;
		properties.autoExtendMaxSize = this.autoExtendMaxSize;
		properties.autoExtendSize = this.autoExtendSize;
		properties.autoSegmentSpaceManagement = this.autoSegmentSpaceManagement;
		properties.dataFileName = this.dataFileName;
		properties.fileSize = this.fileSize;
		properties.initial = this.initial;
		properties.logging = this.logging;
		properties.maxExtents = this.maxExtents;
		properties.minExtents = this.minExtents;
		properties.minimumExtentSize = this.minimumExtentSize;
		properties.next = this.next;
		properties.offline = this.offline;
		properties.pctIncrease = this.pctIncrease;
		properties.temporary = this.temporary;

		return properties;
	}

	public LinkedHashMap<String, String> getPropertiesMap() {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();

		map.put("label.tablespace.data.file", this.getDataFileName());
		map.put("label.size", this.getFileSize());
		map.put("label.tablespace.auto.extend", String.valueOf(this
				.isAutoExtend()));
		map.put("label.size", this.getAutoExtendSize());
		map.put("label.max.size", this.getAutoExtendMaxSize());
		map.put("label.tablespace.minimum.extent.size", this
				.getMinimumExtentSize());
		map.put("label.tablespace.initial", this.getInitial());
		map.put("label.tablespace.next", this.getNext());
		map.put("label.tablespace.min.extents", this.getMinExtents());
		map.put("label.tablespace.pct.increase", this.getPctIncrease());
		map.put("label.tablespace.logging", String.valueOf(this.isLogging()));
		map.put("label.tablespace.offline", String.valueOf(this.isOffline()));
		map.put("label.tablespace.temporary", String
				.valueOf(this.isTemporary()));
		map.put("label.tablespace.auto.segment.space.management", String
				.valueOf(this.isAutoSegmentSpaceManagement()));

		return map;
	}

	public List<String> validate() {
		List<String> errorMessage = new ArrayList<String>();

		if (this.isAutoExtend() && Check.isEmptyTrim(this.getAutoExtendSize())) {
			errorMessage.add("error.tablespace.auto.extend.size.empty");
		}

		return errorMessage;
	}

	public String buildSQL() {
		// CREATE SMALLFILE TABLESPACE "TZENG" DATAFILE
		// 'D:\APP\HANXINHANG\ORADATA\ORCL\DZENG' SIZE 10M REUSE AUTOEXTEND ON
		// NEXT 100K MAXSIZE UNLIMITED LOGGING EXTENT MANAGEMENT LOCAL SEGMENT
		// SPACE MANAGEMENT AUTO
		StringBuilder sql = new StringBuilder();
		sql.append(" CREATE TABLESPACE ").append(tablespaceName).append(" DATAFILE '").append(dataFileDirectory);
		if (!dataFileDirectory.trim().endsWith("\\")) {
			sql.append("\\");
		}
		sql.append(dataFileName).append("'");
		sql.append(" SIZE ").append(fileSize);
		if (isReuse()) {
			sql.append(" REUSE ");
		}
		if (isAutoExtend()) {
			sql.append(" AUTOEXTEND ON NEXT ").append(autoExtendSize);
			sql.append(" MAXSIZE ");
			if (isUnlimited() && null == maxExtents) {
				sql.append(" UNLIMITED ");
			} else {
				sql.append(maxExtents);
			}
		}
		return sql.toString();
	}

	public boolean isReuse() {
		return reuse;
	}

	public void setReuse(boolean selected) {
		this.reuse = selected;
	}

}
