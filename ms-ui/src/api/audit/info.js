import request from '@/utils/request'

// 查询审核信息列表
export function listInfo(query) {
  return request({
    url: '/audit/info/list',
    method: 'get',
    params: query
  })
}

// 查询审核信息详细
export function getInfo(id) {
  return request({
    url: '/audit/info/' + id,
    method: 'get'
  })
}

// 查询历史审批
export function listHistory(id) {
  return request({
    url: '/audit/info/' + id + '/histories',
    method: 'get'
  })
}

// 查看进度图片
export function getProcessingImage(id) {
  return request({
    url: '/audit/info/' + id + '/processing',
    method: 'get'
  })
}

// 新增审核信息
export function startAudit(data) {
  return request({
    url: '/audit/info',
    method: 'post',
    data: data
  })
}

// 修改审核信息
export function updateInfo(data) {
  return request({
    url: '/audit/info',
    method: 'put',
    data: data
  })
}

// 删除审核信息
export function delInfo(id) {
  return request({
    url: '/audit/info/' + id,
    method: 'delete'
  })
}
