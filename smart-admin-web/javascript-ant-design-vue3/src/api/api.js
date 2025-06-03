import { getRequest, postRequest, putRequest, deleteRequest } from '/@/lib/axios-erp';

//首页统计
export const getBuyAndSaleStatistics = (params) => getRequest('/depotHead/getBuyAndSaleStatistics', params);
export const buyOrSalePrice = (params) => getRequest('/depotItem/buyOrSalePrice', params);
//租户管理
export const checkTenant = (params) => getRequest('/tenant/checkIsNameExist', params);
export const addTenant = (params) => postRequest('/tenant/add', params);
export const editTenant = (params) => putRequest('/tenant/update', params);
//角色管理
export const addRole = (params) => postRequest('/role/add', params);
export const editRole = (params) => putRequest('/role/update', params);
export const checkRole = (params) => getRequest('/role/checkIsNameExist', params);
export const roleAllList = (params) => getRequest('/role/allList', params);
export const getTenantRoleList = (params) => getRequest('/role/tenantRoleList', params);
//用户管理
export const registerUser = (params) => postRequest('/user/registerUser', params);
export const addUser = (params) => postRequest('/user/addUser', params);
export const editUser = (params) => putRequest('/user/updateUser', params);
export const getUserList = (params) => getRequest('/user/getUserList', params);
export const getUserBtnByCurrentUser = (params) => getRequest('/user/getUserBtnByCurrentUser', params);
export const queryPermissionsByUser = (params) => postRequest('/function/findMenuByPNumber', params);
//机构管理
export const queryOrganizationTreeList = (params) => getRequest('/organization/getOrganizationTree', params);
export const getAllOrganizationTreeByUser = (params) => getRequest('/organization/getAllOrganizationTreeByUser', params);
export const queryOrganizationById = (params) => getRequest('/organization/findById', params);
export const checkOrganization = (params) => getRequest('/organization/checkIsNameExist', params);
//经手人管理
export const addPerson = (params) => postRequest('/person/add', params);
export const editPerson = (params) => putRequest('/person/update', params);
export const checkPerson = (params) => getRequest('/person/checkIsNameExist', params);
export const getPersonByType = (params) => getRequest('/person/getPersonByType', params);
export const getPersonByNumType = (params) => getRequest('/person/getPersonByNumType', params);
//账户管理
export const addAccount = (params) => postRequest('/account/add', params);
export const editAccount = (params) => putRequest('/account/update', params);
export const checkAccount = (params) => getRequest('/account/checkIsNameExist', params);
export const getAccount = (params) => getRequest('/account/getAccount', params);
//收支项目
export const addInOutItem = (params) => postRequest('/inOutItem/add', params);
export const editInOutItem = (params) => putRequest('/inOutItem/update', params);
export const checkInOutItem = (params) => getRequest('/inOutItem/checkIsNameExist', params);
export const findInOutItemByParam = (params) => getRequest('/inOutItem/findBySelect', params);
//仓库信息
export const addDepot = (params) => postRequest('/depot/add', params);
export const editDepot = (params) => putRequest('/depot/update', params);
export const checkDepot = (params) => getRequest('/depot/checkIsNameExist', params);
//商品属性
export const addOrUpdateMaterialProperty = (params) => postRequest('/materialProperty/addOrUpdate', params);
//商品类型
export const queryMaterialCategoryTreeList = (params) => getRequest('/materialCategory/getMaterialCategoryTree', params);
export const queryMaterialCategoryById = (params) => getRequest('/materialCategory/findById', params);
export const checkMaterialCategory = (params) => getRequest('/materialCategory/checkIsNameExist', params);
export const addMaterialCategory = (params) => postRequest('/materialCategory/add', params);
export const updateMaterialCategory = (params) => putRequest('/materialCategory/update', params);
export const deleteMaterialCategory = (params) => deleteRequest('/materialCategory/delete', params);

//商品管理
export const addMaterial = (params) => postRequest('/material/add', params);
export const editMaterial = (params) => putRequest('/material/update', params);
export const checkMaterial = (params) => getRequest('/material/checkIsExist', params);
export const getMaterialBySelect = (params) => getRequest('/material/findBySelect', params);
export const getSerialMaterialBySelect = (params) => getRequest('/material/getMaterialEnableSerialNumberList', params);
export const getMaterialByParam = (params) => getRequest('/material/getMaterialByParam', params);
export const getMaterialByBarCode = (params) => getRequest('/material/getMaterialByBarCode', params);
export const getMaxBarCode = (params) => getRequest('/material/getMaxBarCode', params);
export const checkMaterialBarCode = (params) => getRequest('/materialsExtend/checkIsBarCodeExist', params);
export const batchUpdateMaterial = (params) => postRequest('/material/batchUpdate', params);
export const changeNameToPinYin = (params) => postRequest('/material/changeNameToPinYin', params);
//序列号
export const batAddSerialNumber = (params) => postRequest('/serialNumber/batAddSerialNumber', params);
export const getEnableSerialNumberList = (params) => getRequest('/serialNumber/getEnableSerialNumberList', params);
//多属性
export const addMaterialAttribute = (params) => postRequest('/materialAttribute/add', params);
export const editMaterialAttribute = (params) => putRequest('/materialAttribute/update', params);
export const checkMaterialAttribute = (params) => getRequest('/materialAttribute/checkIsNameExist', params);
export const getMaterialAttributeNameList = (params) => getRequest('/materialAttribute/getNameList', params);
export const getMaterialAttributeValueListById = (params) => getRequest('/materialAttribute/getValueListById', params);
//功能管理
export const addFunction = (params) => postRequest('/function/add', params);
export const editFunction = (params) => putRequest('/function/update', params);
export const checkFunction = (params) => getRequest('/function/checkIsNameExist', params);
export const checkNumber = (params) => getRequest('/function/checkIsNumberExist', params);
//系统配置
export const addSystemConfig = (params) => postRequest('/systemConfig/add', params);
export const editSystemConfig = (params) => putRequest('/systemConfig/update', params);
export const checkSystemConfig = (params) => getRequest('/systemConfig/checkIsNameExist', params);
export const getCurrentSystemConfig = (params) => getRequest('/systemConfig/getCurrentInfo', params);
export const fileSizeLimit = (params) => getRequest('/systemConfig/fileSizeLimit', params);
//平台参数
export const addPlatformConfig = (params) => postRequest('/platformConfig/add', params);
export const editPlatformConfig = (params) => putRequest('/platformConfig/update', params);
export const getPlatformConfigByKey = (params) => getRequest('/platformConfig/getInfoByKey', params);
//用户|角色|模块关系
export const addUserBusiness = (params) => postRequest('/userBusiness/add', params);
export const editUserBusiness = (params) => putRequest('/userBusiness/update', params);
export const checkUserBusiness = (params) => getRequest('/userBusiness/checkIsValueExist', params);
export const updateBtnStrByRoleId = (params) => postRequest('/userBusiness/updateBtnStr', params);
//多单位
export const addUnit = (params) => postRequest('/unit/add', params);
export const editUnit = (params) => putRequest('/unit/update', params);
export const checkUnit = (params) => getRequest('/unit/checkIsNameExist', params);
//供应商|客户|会员
export const addSupplier = (params) => postRequest('/supplier/add', params);
export const editSupplier = (params) => putRequest('/supplier/update', params);
export const checkSupplier = (params) => getRequest('/supplier/checkIsNameAndTypeExist', params);
export const findBySelectSup = (params) => postRequest('/supplier/findBySelect_sup', params);
export const findBySelectCus = (params) => postRequest('/supplier/findBySelect_cus', params);
export const findBySelectRetail = (params) => postRequest('/supplier/findBySelect_retail', params);
export const findBySelectOrgan = (params) => postRequest('/supplier/findBySelect_organ', params);
//单据相关
export const findBillDetailByNumber = (params) => getRequest('/depotHead/getDetailByNumber', params);
export const waitBillCount = (params) => getRequest('/depotHead/waitBillCount', params);
export const getNeedCount = (params) => getRequest('/depotHead/getNeedCount', params);
export const batchAddDepotHeadAndDetail = (params) => postRequest('/depotHead/batchAddDepotHeadAndDetail', params);
export const findStockByDepotAndBarCode = (params) => getRequest('/depotItem/findStockByDepotAndBarCode', params);
export const getBatchNumberList = (params) => getRequest('/depotItem/getBatchNumberList', params);
export const findFinancialDetailByNumber = (params) => getRequest('/accountHead/getDetailByNumber', params);
export const findBillPrintTemplate = (params) => getRequest('/print/findBillPrintTemplate', params);
export const upsertBillPrintTemplate = (params) => postRequest('/print/upsertBillPrintTemplate', params);
