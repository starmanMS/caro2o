import request, {upload} from '@/utils/request'

// 查询流程定义列表
export function listBpmnInfo(query) {
  return request({
    url: '/flow/bpmnInfo/list',
    method: 'get',
    params: query
  })
}

// 查询流程定义详细
export function getBpmnInfo(id) {
  return request({
    url: '/flow/bpmnInfo/' + id,
    method: 'get'
  })
}

// 新增流程定义
export function addBpmnInfo(data) {
  return request({
    url: '/flow/bpmnInfo',
    method: 'post',
    data: data
  })
}

// 部署流程定义
export function deployBpmn(data) {
  return upload('/flow/bpmnInfo/deploy', data);
}

// 从后端查询 xml 文件
export function getBpmnFile(id, type, config) {
  return request({
    url: `/flow/bpmnInfo/${id}/${type}`,
    method: 'get',
    ...config
  });
}

// 修改流程定义
export function updateBpmnInfo(data) {
  return request({
    url: '/flow/bpmnInfo',
    method: 'put',
    data: data
  })
}

// 删除流程定义
export function delBpmnInfo(id) {
  return request({
    url: '/flow/bpmnInfo/' + id,
    method: 'delete'
  })
}
