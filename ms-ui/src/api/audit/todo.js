import request from '@/utils/request'

// 查询待办任务列表
export function todoList(query) {
  return request({
    url: '/audit/todo/list',
    method: 'get',
    params: query
  })
}

// 处理审批
export function doAudit(data) {
  return request({
    url: '/audit/todo/handle',
    method: 'put',
    data: data
  })
}
