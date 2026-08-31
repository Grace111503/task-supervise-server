declare namespace User {
  interface UserInfo {
    id?: number
    token?: string
    user_id?: number
    name?: string
    avatar?: string
    role?: string
    /** 角色描述（中文）: 督办管理员/部门主管/普通执行人员 */
    roleDesc?: string
    /** 所属部门ID */
    deptId?: number
    /** 职位 */
    position?: string
    phone?: string
    email?: string
  }
}
