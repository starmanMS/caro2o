import request from '@/utils/request'

// 查询结算单列表
export function listStatement(query) {
  return request({
    url: '/appointment/statement/list',
    method: 'get',
    params: query
  })
}

// 查询结算单详细
export function getStatement(id) {
  return request({
    url: '/appointment/statement/' + id,
    method: 'get'
  })
}

// 查询结算单明细
export function getStatementItems(id) {
  return request({
    url: '/appointment/statement/items/' + id,
    method: 'get'
  })
}

// 新增结算单明细
export function addStatementItems(data) {
  return request({
    url: '/appointment/statement/items',
    method: 'post',
    data: data
  })
}

// 新增结算单
export function addStatement(data) {
  return request({
    url: '/appointment/statement',
    method: 'post',
    data: data
  })
}

// 支付
export function prepay(id) {
  return request({
    url: '/appointment/statement/prepay/' + id,
    method: 'post',
  })
}

// 修改结算单
export function updateStatement(data) {
  return request({
    url: '/appointment/statement',
    method: 'put',
    data: data
  })
}

// 删除结算单
export function delStatement(id) {
  return request({
    url: '/appointment/statement/' + id,
    method: 'delete'
  })
}
