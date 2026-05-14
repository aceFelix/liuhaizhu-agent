package com.itfelix.liuhaizhuaichat.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itfelix.liuhaizhuaichat.mapper.FileRecordMapper;
import com.itfelix.liuhaizhuaichat.pojo.entity.FileRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.recorder.FileRecorder;
import org.dromara.x.file.storage.core.upload.FilePartInfo;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * x-file-storage 文件记录器实现类
 * 用于记录上传的文件信息到数据库
 * @author aceFelix
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileRecorderImpl implements FileRecorder {

    private final FileRecordMapper fileRecordMapper;

    /**
     * 保存文件信息
     *
     * @param fileInfo 文件信息
     * @return 是否保存成功
     */
    @Override
    public boolean save(FileInfo fileInfo) {
        try {
            FileRecord record = convertToFileRecord(fileInfo);
            record.setCreateTime(LocalDateTime.now());
            record.setUpdateTime(LocalDateTime.now());
            fileRecordMapper.insert(record);
            log.debug("保存文件记录成功: {}", fileInfo.getUrl());
            return true;
        } catch (Exception e) {
            log.error("保存文件记录失败: {}", fileInfo.getUrl(), e);
            return false;
        }
    }

    @Override
    public void update(FileInfo fileInfo) {

    }

    /**
     * 根据 URL 获取文件信息
     *
     * @param url 文件访问地址
     * @return 文件信息
     */
    @Override
    public FileInfo getByUrl(String url) {
        try {
            LambdaQueryWrapper<FileRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FileRecord::getUrl, url);
            FileRecord record = fileRecordMapper.selectOne(wrapper);
            if (record != null) {
                return convertToFileInfo(record);
            }
        } catch (Exception e) {
            log.error("获取文件记录失败: {}", url, e);
        }
        return null;
    }

    /**
     * 根据 URL 删除文件信息
     *
     * @param url 文件访问地址
     * @return 是否删除成功
     */
    @Override
    public boolean delete(String url) {
        try {
            LambdaQueryWrapper<FileRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FileRecord::getUrl, url);
            int result = fileRecordMapper.delete(wrapper);
            log.debug("删除文件记录成功: {}, 影响行数: {}", url, result);
            return result > 0;
        } catch (Exception e) {
            log.error("删除文件记录失败: {}", url, e);
            return false;
        }
    }

    @Override
    public void saveFilePart(FilePartInfo filePartInfo) {

    }

    @Override
    public void deleteFilePartByUploadId(String s) {

    }

    /**
     * 将 FileInfo 转换为 FileRecord
     *
     * @param fileInfo 文件信息
     * @return 文件记录实体
     */
    private FileRecord convertToFileRecord(FileInfo fileInfo) {
        FileRecord record = new FileRecord();
        record.setUrl(fileInfo.getUrl());
        record.setSize(fileInfo.getSize());
        record.setFilename(fileInfo.getFilename());
        record.setOriginalFilename(fileInfo.getOriginalFilename());
        record.setBasePath(fileInfo.getBasePath());
        record.setPath(fileInfo.getPath());
        record.setExt(fileInfo.getExt());
        record.setContentType(fileInfo.getContentType());
        record.setPlatform(fileInfo.getPlatform());
        record.setThUrl(fileInfo.getThUrl());
        record.setThFilename(fileInfo.getThFilename());
        record.setThSize(fileInfo.getThSize());
        record.setThContentType(fileInfo.getThContentType());
        record.setObjectId(fileInfo.getObjectId());
        record.setObjectType(fileInfo.getObjectType());
        record.setAttr(fileInfo.getAttr() != null ? fileInfo.getAttr().toString() : null);
        return record;
    }

    /**
     * 将 FileRecord 转换为 FileInfo
     *
     * @param record 文件记录实体
     * @return 文件信息
     */
    private FileInfo convertToFileInfo(FileRecord record) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setUrl(record.getUrl());
        fileInfo.setSize(record.getSize());
        fileInfo.setFilename(record.getFilename());
        fileInfo.setOriginalFilename(record.getOriginalFilename());
        fileInfo.setBasePath(record.getBasePath());
        fileInfo.setPath(record.getPath());
        fileInfo.setExt(record.getExt());
        fileInfo.setContentType(record.getContentType());
        fileInfo.setPlatform(record.getPlatform());
        fileInfo.setThUrl(record.getThUrl());
        fileInfo.setThFilename(record.getThFilename());
        fileInfo.setThSize(record.getThSize());
        fileInfo.setThContentType(record.getThContentType());
        fileInfo.setObjectId(record.getObjectId());
        fileInfo.setObjectType(record.getObjectType());
        return fileInfo;
    }
}
