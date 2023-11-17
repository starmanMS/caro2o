import request from '@/utils/request'

// 查询服务项列表
export function listServiceItem(query) {
  return request({
    url: '/appointment/serviceItem/list',
    method: 'get',
    params: query
  })
}

// 查询服务项详细
export function getServiceItem(id) {
  return request({
    url: '/appointment/serviceItem/' + id,
    method: 'get'
  })
}

export function getServiceItemAuditInfo(id) {
  return request({
    url: '/appointment/serviceItem/auditInfo/' + id,
    method: 'get'
  })
}

// 新增服务项
export function addServiceItem(data) {
  return request({
    url: '/appointment/serviceItem',
    method: 'post',
    data: data
  })
}

// 修改服务项
export function updateServiceItem(data) {
  return request({
    url: '/appointment/serviceItem',
    method: 'put',
    data: data
  })
}

// 服务项上下架
export function updateSaleStatus(id) {
  return request({
    url: '/appointment/serviceItem/saleStatus/' + id,
    method: 'put'
  })
}

// 删除服务项
export function delServiceItem(id) {
  return request({
    url: '/appointment/serviceItem/' + id,
    method: 'delete'
  })
}
