import request from '@/utils/request'

// 查询养修信息预约列表
export function listInfo(query) {
  return request({
    url: '/appointment/info/list',
    method: 'get',
    params: query
  })
}

// 查询养修信息预约详细
export function getInfo(id) {
  return request({
    url: '/appointment/info/' + id,
    method: 'get'
  })
}

// 新增养修信息预约
export function addInfo(data) {
  return request({
    url: '/appointment/info',
    method: 'post',
    data: data
  })
}

// 修改养修信息预约
export function updateInfo(data) {
  return request({
    url: '/appointment/info',
    method: 'put',
    data: data
  })
}

// 客户到店接口
export function arrivalInfo(id) {
  return request({
    url: '/appointment/info/arrival/' + id,
    method: 'put'
  })
}

// 取消预约接口
export function cancelInfo(id) {
  return request({
    url: '/appointment/info/cancel/' + id,
    method: 'put'
  })
}

// 创建结算单
export function createStatement(id) {
  return request({
    url: `/appointment/info/${id}/statements`,
    method: 'put'
  })
}
