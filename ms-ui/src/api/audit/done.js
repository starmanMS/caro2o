import request from '@/utils/request'

// 查询已办任务列表
export function doneList(query) {
  return request({
    url: '/audit/done/list',
    method: 'get',
    params: query
  })
}
