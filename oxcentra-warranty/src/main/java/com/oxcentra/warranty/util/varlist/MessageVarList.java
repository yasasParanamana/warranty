package com.oxcentra.warranty.util.varlist;

/**
 * @author Namila Withanage on 11/19/2021
 */
public class MessageVarList {

    //-------------------------- start common messages----------------------------------------------------------------//
    public static final String COMMON_ERROR_PROCESS = "common.error.process";
    public static final String COMMON_ERROR_RECORD_DOESNOT_EXISTS = "common.error.record.doesnot.exists";
    public static final String COMMON_ERROR_NO_VALUE_CHANGE = "common.error.no.value.change";
    public static final String COMMON_ERROR_ALRADY_USE = "common.error.alreadyuse";
    //-------------------------- end common messages------------------------------------------------------------------//

    //-------------------------- start user login messages------------------------------------------------------------//
    public static final String LOGIN_USERNAME_EMPTY = "login.username.empty";
    public static final String LOGIN_USERNAME_INVALID = "login.username.invalid";
    public static final String LOGIN_PASSWORD_EMPTY = "login.password.empty";
    public static final String LOGIN_INVALID = "login.invalid";
    public static final String LOGIN_DEACTIVE = "login.deactive";
    public static final String LOGIN_IDLEDEACTIVE = "login.idledeactive";
    public static final String LOGIN_EXPIRYWARNING = "login.expirywarning";
    public static final String LOGIN_STATUSTIMEOUT = "login.statustimeout";
    //-------------------------- end user login messages--------------------------------------------------------------//

    //-------------------------- start password reset mgt-------------------------------------------------------------//
    public static final String PASSWORDRESET_NEWUSER = "passwordreset.newuser";
    public static final String PASSWORDRESET_RESETUSER = "passwordreset.resetuser";
    public static final String PASSWORDRESET_CHANGEPWD = "passwordreset.changepwd";
    public static final String PASSWORDRESET_EXPPWD = "passwordreset.exppwd";
    public static final String PASSWORDRESET_SUCCESS = "passwordreset.changepwd.success";
    //-------------------------- start password reset mgt-------------------------------------------------------------//

    //-------------------------- task mgt-----------------------------------------------------------------------------//
    public static final String TASK_MGT_EMPTY_TASKCODE = "task.empty.taskcode";
    public static final String TASK_MGT_EMPTY_DESCRIPTION = "task.empty.description";
    public static final String TASK_MGT_EMPTY_STATUS = "task.empty.status";
    public static final String TASK_MGT_SUCCESS_ADD = "task.success.add";
    public static final String TASK_MGT_SUCCESS_UPDATE = "task.success.update";
    public static final String TASK_MGT_SUCCESS_DELETE = "task.success.delete";
    public static final String TASK_MGT_SUCCESS_CONFIRM = "task.success.confirm";
    public static final String TASK_MGT_SUCCESS_REJECT = "task.success.reject";
    public static final String TASK_MGT_ALREADY_EXISTS = "task.already.exists";
    public static final String TASK_MGT_NORECORD_FOUND = "";
    //-------------------------- task mgt-----------------------------------------------------------------------------//

    //-------------------------- start tmp record messages------------------------------------------------------------//
    public static final String TMP_RECORD_ALREADY_EXISTS = "tmp.record.already.exists";
    //-------------------------- start tmp record messages------------------------------------------------------------//

    //-------------------------- start user password change ----------------------------------------------------------//
    public static final String USER_PASSWORDS_MISMATCH = "user.password.mismatch";
    public static final String USER_CURRENTPASSWORD_EMPTY = "user.currentpassword.empty";
    public static final String USER_CURRENTPASSWORD_INVALID = "user.currentpassword.invalid";
    public static final String USER_NEWPASSWORD_EMPTY = "user.newpassword.empty";
    public static final String USER_NEWCONFIRMPASSWORD_EMPTY = "user.newconfirmpassword.empty";
    public static final String USER_NEWPASSWORD_TOO_SHORT = "user.newpassword.tooshort";
    public static final String USER_NEWPASSWORD_TOO_LONG = "user.newpassword.toolong";
    public static final String USER_NEWPASSWORD_LESS_SPECIALCHARACTERS = "user.newpassword.less.specialcharacters";
    public static final String USER_NEWPASSWORD_LESS_UPPERCHARACTERS = "user.newpassword.less.uppercharacters";
    public static final String USER_NEWPASSWORD_LESS_LOWERCHARACTERS = "user.newpassword.less.lowercharacters";
    public static final String USER_NEWPASSWORD_LESS_NUMERICCHARACTERS = "user.newpassword.less.numericcharacters";
    public static final String USER_NEWPASSWORD_MORE_REPEATCHARACTERS = "user.newpassword.more.repeatcharacters";
    public static final String USER_NEWPASSWORD_EXIST_PASSWORDHISTORY = "user.newpassword.exist.passwordhistory";
    public static final String USER_NEWPASSWORD_RESET_ERROR = "user.newpassword.reset.error";
    public static final String USER_SESSION_NOTFOUND = "user.session.notfound";
    public static final String USER_SESSION_INVALID = "user.session.invalid";
    public static final String USER_SESSION_TIMEOUT = "user.session.timeout";
    public static final String USER_REQUESTED_PASSWORDCHANGE = "user.requested.passwordchange";
    public static final String USER_PRIVILEGE_INSUFFICIENT = "user.privilege.insufficient";
    //-------------------------- start user password change-----------------------------------------------------------//

    //-------------------------- start page mgt-----------------------------------------------------------------------//
    public static final String PAGE_MGT_EMPTY_PAGECODE = "page.empty.pagecode";
    public static final String PAGE_MGT_EMPTY_DESCRIPTION = "page.empty.description";
    public static final String PAGE_MGT_EMPTY_SORTKEY = "page.empty.sortkey";
    public static final String PAGE_MGT_SORTKEY_EXIST = "page.exist.sortkey";
    public static final String PAGE_MGT_SORTKEY_TEMP_EXIST = "page.temp.exist.sortkey";
    public static final String PAGE_MGT_EMPTY_STATUS = "page.empty.status";
    public static final String PAGE_MGT_SUCCESS_UPDATE = "page.success.update";
    public static final String PAGE_MGT_ERROR_UPDATE = "page.error.update";
    public static final String PAGE_MGT_SUCCESS_CONFIRM = "page.success.confirm";
    public static final String PAGE_MGT_SUCCESS_REJECT = "page.success.reject";
    public static final String PAGE_MGT_NORECORD_FOUND = "";
    //-------------------------- start page mgt-----------------------------------------------------------------------//

    //-------------------------- start userrole mgt-------------------------------------------------------------------//
    public static final String USERROLE_MGT_EMPTY_USERROLECODE = "userrole.empty.userrolecode";
    public static final String USERROLE_MGT_EMPTY_DESCRIPTION = "userrole.empty.description";
    public static final String USERROLE_MGT_EMPTY_USERROLETYPE = "userrole.empty.userroletype";
    public static final String USERROLE_MGT_EMPTY_STATUS = "userrole.empty.status";
    public static final String USERROLE_MGT_SUCCESS_ADD = "userrole.success.add";
    public static final String USERROLE_MGT_ALREADY_EXISTS = "userrole.already.exists";
    public static final String USERROLE_MGT_ERROR_ADD = "userrole.error.add";
    public static final String USERROLE_MGT_SUCCESS_UPDATE = "userrole.success.update";
    public static final String USERROLE_MGT_ERROR_UPDATE = "userrole.success.update";
    public static final String USERROLE_MGT_SUCCESS_DELETE = "userrole.success.delete";
    public static final String USERROLE_MGT_ERROR_DELETE = "userrole.success.delete";
    public static final String USERROLE_MGT_EMPTY_PAGE = "userrole.empty.page";
    public static final String USERROLE_MGT_ASSIGNED_PAGE = "userrole.assigned.page";
    public static final String USERROLE_MGT_ERROR_UNASSIGNED_PAGE_TASK = "userrole.error.unassigned.page.task";
    public static final String USERROLE_MGT_ERROR_UNASSIGNED_PAGE = "userrole.error.unassigned.page";
    public static final String USERROLE_MGT_ERROR_ASSIGNED_PAGE = "userrole.error.assigned.page";
    public static final String USERROLE_MGT_SUCCESS_ASSIGNED_PAGE = "userrole.success.assigned.page";
    public static final String USERROLE_MGT_SUCCESS_CONFIRM = "userrole.success.confirm";
    public static final String USERROLE_MGT_SUCCESS_REJECT = "userrole.success.reject";
    public static final String USERROLE_MGT_SUCCESS_ASSIGNED_TASK = "userrole.success.assigned.task";
    public static final String USERROLE_MGT_EMPTY_TASK = "userrole.empty.task";
    public static final String USERROLE_MGT_ASSIGNED_TASK = "userrole.assigned.task";
    public static final String USERROLE_MGT_ERROR_UNASSIGNED_TASK = "userrole.error.unassigned.task";
    public static final String USERROLE_MGT_ERROR_ASSIGNED_TASK = "userrole.error.assigned.task";
    public static final String USERROLE_MGT_NORECORD_FOUND = "userrole.norecord.found";
    //-------------------------- start userrole mgt-------------------------------------------------------------------//

    //-------------------------- password param mgt-------------------------------------------------------------------//
    public static final String PASSWORD_PARAM_MGT_EMPTY_PASSWORDPARAM = "passwordparam.empty.passwordparam";
    public static final String PASSWORD_PARAM_MGT_EMPTY_USERROLETYPE = "passwordparam.empty.userroletype";
    public static final String PASSWORD_PARAM_MGT_EMPTY_VALUE = "passwordparam.empty.value";
    public static final String PASSWORD_PARAM_MGT_SUCCESS_UPDATE = "passwordparam.success.update";
    public static final String PASSWORD_PARAM_MGT_SUCCESS_CONFIRM = "passwordparam.success.confirm";
    public static final String PASSWORD_PARAM_MGT_SUCCESS_REJECT = "passwordparam.success.reject";
    public static final String PASSWORD_PARAM_MGT_ALREADY_EXISTS = "passwordparam.already.exists";
    public static final String PASSWORD_PARAM_MGT_NORECORD_FOUND = "passwordparam.norecord.found";
    //-------------------------- password param mgt-------------------------------------------------------------------//

    //-------------------------- password policy mgt------------------------------------------------------------------//
    public static final String PASSWORD_POLICY_MGT_EMPTY_PASSWORDPOLICYID = "passwordpolicy.empty.passwordPolicyId";
    public static final String PASSWORD_POLICY_MGT_EMPTY_MINIMUM_LENGTH = "passwordpolicy.empty.minimumLength";
    public static final String PASSWORD_POLICY_MGT_EMPTY_MAXIMUM_LENGTH = "passwordpolicy.empty.maximumLength";
    public static final String PASSWORD_POLICY_MGT_EMPTY_MINIMUM_SPECIAL_CHARACTERS = "passwordpolicy.empty.minimumSpecialCharacters";
    public static final String PASSWORD_POLICY_MGT_EMPTY_MINIMUM_UPPERCASE_CHARACTERS = "passwordpolicy.empty.minimumUpperCaseCharacters";
    public static final String PASSWORD_POLICY_MGT_EMPTY_MINIMUM_NUMERICAL_CHARACTERS = "passwordpolicy.empty.minimumNumericalCharacters";
    public static final String PASSWORD_POLICY_MGT_EMPTY_MINIMUM_LOWERCASE_CHARACTERS = "passwordpolicy.empty.minimumLowerCaseCharacters";
    public static final String PASSWORD_POLICY_MGT_EMPTY_INVALID_LOGIN_ATTEMPTS = "passwordpolicy.empty.noOfInvalidLoginAttempt";
    public static final String PASSWORD_POLICY_MGT_EMPTY_REPEAT_CHARACTER_ALLOW = "passwordpolicy.empty.repeatCharactersAllow";
    public static final String PASSWORD_POLICY_MGT_EMPTY_PASSWORD_EXPIRY_STATUS = "passwordpolicy.empty.initialPasswordExpiryStatus";
    public static final String PASSWORD_POLICY_MGT_EMPTY_PASSWORD_EXPIRY_PERIOD = "passwordpolicy.empty.passwordExpiryPeriod";
    public static final String PASSWORD_POLICY_MGT_EMPTY_NO_OF_HISTORY_PASSWORD = "passwordpolicy.empty.noOfHistoryPassword";
    public static final String PASSWORD_POLICY_MGT_EMPTY_MIN_PASSWORD_CHANGE_PERIOD = "passwordpolicy.empty.minimumPasswordChangePeriod";
    public static final String PASSWORD_POLICY_MGT_EMPTY_IDLE_ACCOUNT_EXPIRY_PERIOD = "passwordpolicy.empty.idleAccountExpiryPeriod";
    public static final String PASSWORD_POLICY_MGT_EMPTY_DESCRIPTION = "passwordpolicy.empty.description";
    public static final String PASSWORD_POLICY_MGT_EMPTY_VALUE = "passwordpolicy.empty.value";
    public static final String PASSWORD_POLICY_MGT_SUCCESS_UPDATE = "passwordpolicy.success.update";
    public static final String PASSWORD_POLICY_MGT_SUCCESS_CONFIRM = "passwordpolicy.success.confirm";
    public static final String PASSWORD_POLICY_MGT_SUCCESS_REJECT = "passwordpolicy.success.reject";
    public static final String PASSWORD_POLICY_MGT_ALREADY_EXISTS = "passwordpolicy.already.exists";
    public static final String PASSWORD_POLICY_MGT_NORECORD_FOUND = "";
    public static final String PASSPOLICY_MINLEN_INVALID = "passwordpolicy.invalid.minimumLength";
    public static final String PASSPOLICY_MAXLEN_INVALID = "passwordpolicy.invalid.maximumLength";
    public static final String PASSPOLICY_NO_OF_HISTORY_PASSWORD_INVALID = "passwordpolicy.invalid.noOfHistoryPassword";
    public static final String PASSPOLICY_PASSWORD_EXPIRY_PERIOD_INVALID = "passwordpolicy.invalid.passwordExpiryPeriod";
    public static final String PASSPOLICY_IDLE_ACCOUNT_EXPIRY_PERIOD_INVALID = "passwordpolicy.invalid.idleAccountExpiryPeriod";
    public static final String PASSPOLICY_MIN_MAX_LENGHT_DIFF = "passwordpolicy.invalid.minMaxLengthDiff";
    //-------------------------- password policy mgt------------------------------------------------------------------//

    //-------------------------- system user mgt----------------------------------------------------------------------//
    public static final String SYSTEMUSER_MGT_EMPTY_USERNAME = "systemuser.empty.username";
    public static final String SYSTEMUSER_MGT_EMPTY_FULLNAME = "systemuser.empty.fullname";
    public static final String SYSTEMUSER_MGT_EMPTY_EMAIL = "systemuser.empty.email";
    public static final String SYSTEMUSER_MGT_INVALID_EMAIL = "systemuser.invalid.email";
    public static final String SYSTEMUSER_MGT_EMPTY_USERROLECODE = "systemuser.empty.userrolecode";
    public static final String SYSTEMUSER_MGT_EMPTY_STATUS = "systemuser.empty.status";
    public static final String SYSTEMUSER_MGT_EMPTY_MOBILENUMBER = "systemuser.empty.mobilenumber";
    public static final String SYSTEMUSER_MGT_EMPTY_NIC = "systemuser.empty.nic";
    public static final String SYSTEMUSER_MGT_INVALID_NIC = "systemuser.invalid.nic";
    public static final String SYSTEMUSER_MGT_EMPTY_SERVICE_ID = "systemuser.empty.serviceid";
    public static final String SYSTEMUSER_MGT_EMPTY_PASSWORD = "systemuser.empty.password";
    public static final String SYSTEMUSER_MGT_EMPTY_CONFIRMPASSWORD = "systemuser.empty.confirmpassword";
    public static final String SYSTEMUSER_MGT_PASSWORDS_MISMATCH = "systemuser.password.mismatch";
    public static final String SYSTEMUSER_MGT_ADDED_SUCCESSFULLY = "systemuser.added.success";
    public static final String SYSTEMUSER_MGT_UPDATE_SUCCESSFULLY = "systemuser.updated.success";
    public static final String SYSTEMUSER_MGT_DELETE_SUCCESSFULLY = "systemuser.delete.success";
    public static final String SYSTEMUSER_MGT_CONFIRM_SUCCESSFULLY = "systemuser.confirm.success";
    public static final String SYSTEMUSER_MGT_REJECT_SUCCESSFULLY = "systemuser.reject.success";
    public static final String SYSTEMUSER_MGT_ALREADY_EXISTS = "systemuser.already.exists";
    public static final String SYSTEMUSER_MGT_NIC_EXISTS = "systemuser.nic.exists";
    public static final String SYSTEMUSER_MGT_SERVICEID_EXISTS = "systemuser.serviceid.exists";
    public static final String SYSTEMUSER_MGT_NORECORD_FOUND = "systemuser.norecord.found";
    //-------------------------- system user mgt----------------------------------------------------------------------//

    //-------------------------- department mgt-----------------------------------------------------------------------//
    public static final String DEPARTMENT_MGT_ADDED_SUCCESSFULLY = "department.added.success";
    public static final String DEPARTMENT_MGT_UPDATE_SUCCESSFULLY = "department.updated.success";
    public static final String DEPARTMENT_MGT_DELETE_SUCCESSFULLY = "department.deleted.success";
    public static final String DEPARTMENT_MGT_CONFIRM_SUCCESSFULLY = "department.confirm.success";
    public static final String DEPARTMENT_MGT_REJECT_SUCCESSFULLY = "department.reject.success";
    public static final String DEPARTMENT_MGT_ALREADY_EXISTS = "department.already.exists";
    public static final String DEPARTMENT_MGT_NORECORD_FOUND = "department.norecord.found";
    public static final String DEPARTMENT_MGT_EMPTY_CODE = "department.empty.code";
    public static final String DEPARTMENT_MGT_EMPTY_DESCRIPTION = "department.empty.description";
    public static final String DEPARTMENT_MGT_EMPTY_STATUS = "department.empty.status";
    //-------------------------- department mgt-----------------------------------------------------------------------//

    //-------------------------- txn type mgt-----------------------------------------------------------------------//
    public static final String TXN_TYPE_MGT_ADDED_SUCCESSFULLY = "txntype.added.success";
    public static final String TXN_TYPE_MGT_UPDATE_SUCCESSFULLY = "txntype.updated.success";
    public static final String TXN_TYPE_MGT_DELETE_SUCCESSFULLY = "txntype.deleted.success";
    public static final String TXN_TYPE_MGT_CONFIRM_SUCCESSFULLY = "txntype.confirm.success";
    public static final String TXN_TYPE_MGT_REJECT_SUCCESSFULLY = "txntype.reject.success";
    public static final String TXN_TYPE_MGT_ALREADY_EXISTS = "txntype.already.exists";
    public static final String TXN_TYPE_MGT_NORECORD_FOUND = "txntype.norecord.found";
    public static final String TXN_TYPE_MGT_EMPTY_CODE = "txntype.empty.code";
    public static final String TXN_TYPE_MGT_EMPTY_DESCRIPTION = "txntype.empty.description";
    public static final String TXN_TYPE_MGT_EMPTY_STATUS = "txntype.empty.status";
    //-------------------------- txn type mgt-----------------------------------------------------------------------//

    //-------------------------- Channel Txn Type telco mgt-----------------------------------------------------------------------//
    public static final String CHANNEL_TXN_TYPE_ADDED_SUCCESSFULLY = "channeltxntype.added.success";
    public static final String CHANNEL_TXN_TYPE_UPDATE_SUCCESSFULLY = "channeltxntype.updated.success";
    public static final String CHANNEL_TXN_TYPE_DELETE_SUCCESSFULLY = "channeltxntype.deleted.success";
    public static final String CHANNEL_TXN_TYPE_CONFIRM_SUCCESSFULLY = "channeltxntype.confirm.success";
    public static final String CHANNEL_TXN_TYPE_REJECT_SUCCESSFULLY = "channeltxntype.reject.success";
    public static final String CHANNEL_TXN_TYPE_ALREADY_EXISTS = "channeltxntype.already.exists";
    public static final String CHANNEL_TXN_TYPE_NORECORD_FOUND = "channeltxntype.norecord.found";
    public static final String CHANNEL_TXN_TYPE_EMPTY_TXNTYPE = "channeltxntype.empty.txntype";
    public static final String CHANNEL_TXN_TYPE_EMPTY_CHANNEL = "channeltxntype.empty.channel";
    public static final String CHANNEL_TXN_TYPE_EMPTY_TEMPLATE = "channeltxntype.empty.template";
    public static final String CHANNEL_TXN_TYPE_EMPTY_STATUS = "channeltxntype.empty.status";
    //-------------------------- Channel Txn Type mgt-----------------------------------------------------------------------//

    //-------------------------- mt port mgt-----------------------------------------------------------------------//
    public static final String SMSMTPORT_MGT_ADDED_SUCCESSFULLY = "smsmtport.added.success";
    public static final String SMSMTPORT_MGT_UPDATE_SUCCESSFULLY = "smsmtport.updated.success";
    public static final String SMSMTPORT_MGT_DELETE_SUCCESSFULLY = "smsmtport.deleted.success";
    public static final String SMSMTPORT_MGT_CONFIRM_SUCCESSFULLY = "smsmtport.confirm.success";
    public static final String SMSMTPORT_MGT_REJECT_SUCCESSFULLY = "smsmtport.reject.success";
    public static final String SMSMTPORT_MGT_ALREADY_EXISTS = "smsmtport.already.exists";
    public static final String SMSMTPORT_MGT_NORECORD_FOUND = "smsmtport.norecord.found";
    public static final String SMSMTPORT_MGT_EMPTY_SMS_MT_PORT = "smsmtport.empty.mtport";
    public static final String SMSMTPORT_MGT_EMPTY_STATUS = "smsmtport.empty.status";
    //-------------------------- mt port mgt-----------------------------------------------------------------------//

    //-------------------------- category telco mgt-----------------------------------------------------------------------//
    public static final String CATEGORY_TELCO_ADDED_SUCCESSFULLY = "categorytelco.added.success";
    public static final String CATEGORY_TELCO_UPDATE_SUCCESSFULLY = "categorytelco.updated.success";
    public static final String CATEGORY_TELCO_DELETE_SUCCESSFULLY = "categorytelco.deleted.success";
    public static final String CATEGORY_TELCO_CONFIRM_SUCCESSFULLY = "categorytelco.confirm.success";
    public static final String CATEGORY_TELCO_REJECT_SUCCESSFULLY = "categorytelco.reject.success";
    public static final String CATEGORY_TELCO_ALREADY_EXISTS = "categorytelco.already.exists";
    public static final String CATEGORY_TELCO_NORECORD_FOUND = "categorytelco.norecord.found";
    public static final String CATEGORY_TELCO_EMPTY_CATEGORY = "categorytelco.empty.category";
    public static final String CATEGORY_TELCO_EMPTY_TELCO = "categorytelco.empty.telco";
    public static final String CATEGORY_TELCO_EMPTY_MT_PORT = "categorytelco.empty.mtport";
    public static final String CATEGORY_TELCO_EMPTY_STATUS = "categorytelco.empty.status";
    //-------------------------- category telco mgt-----------------------------------------------------------------------//

    //-------------------------- userparam mgt-----------------------------------------------------------------------//
    public static final String USERPARAM_MGT_EMPTY_PARAMCODE = "userparam.empty.paramcode";
    public static final String USERPARAM_MGT_EMPTY_DESCRIPTION = "userparam.empty.description";
    public static final String USERPARAM_MGT_EMPTY_STATUS = "userparam.empty.status";
    public static final String USERPARAM_MGT_EMPTY_CATEGORY = "userparam.empty.category";
    public static final String USERPARAM_MGT_ADDED_SUCCESSFULLY = "userparam.added.success";
    public static final String USERPARAM_MGT_UPDATE_SUCCESSFULLY = "userparam.updated.success";
    public static final String USERPARAM_MGT_DELETE_SUCCESSFULLY = "userparam.deleted.success";
    public static final String USERPARAM_MGT_CONFIRM_SUCCESSFULLY = "userparam.confirm.success";
    public static final String USERPARAM_MGT_REJECT_SUCCESSFULLY = "userparam.reject.success";
    public static final String USERPARAM_MGT_ALREADY_EXISTS = "userparam.already.exists";
    public static final String USERPARAMT_MGT_NORECORD_FOUND = "userparam.norecord.found";
    //-------------------------- userparam mgt------------------------------------------------------------------------//

    //-------------------------- category mgt-------------------------------------------------------------------------//
    public static final String CATEGORY_MGT_ADDED_SUCCESSFULLY = "category.added.success";
    public static final String CATEGORY_MGT_UPDATE_SUCCESSFULLY = "category.updated.success";
    public static final String CATEGORY_MGT_DELETE_SUCCESSFULLY = "category.deleted.success";
    public static final String CATEGORY_MGT_CONFIRM_SUCCESSFULLY = "category.confirm.success";
    public static final String CATEGORY_MGT_REJECT_SUCCESSFULLY = "category.reject.success";
    public static final String CATEGORY_MGT_ALREADY_EXISTS = "category.already.exists";
    public static final String CATEGORY_MGT_NORECORD_FOUND = "category.norecord.found";
    public static final String CATEGORY_MGT_EMPTY_CODE = "category.empty.categoryCode";
    public static final String CATEGORY_MGT_EMPTY_DESCRIPTION = "category.empty.description";
    public static final String CATEGORY_MGT_EMPTY_STATUS = "category.empty.status";
    public static final String CATEGORY_MGT_EMPTY_PRIORITY = "category.empty.priority";
    public static final String CATEGORY_MGT_EMPTY_BULKENABLE = "category.empty.bulkEnable";
    public static final String CATEGORY_MGT_EMPTY_UNSUBSCRIBE = "category.empty.unsubscribe";
    public static final String CATEGORY_MGT_EMPTY_ACKWAIT = "category.empty.ackwait";
    public static final String CATEGORY_MGT_EMPTY_TTLQUEUE = "category.empty.ttlqueue";
    //-------------------------- category mgt-------------------------------------------------------------------------//

    //-------------------------- template mgt-------------------------------------------------------------------------//
    public static final String TEMPLATE_MGT_ADDED_SUCCESSFULLY = "template.added.success";
    public static final String TEMPLATE_MGT_UPDATE_SUCCESSFULLY = "template.updated.success";
    public static final String TEMPLATE_MGT_DELETE_SUCCESSFULLY = "template.deleted.success";
    public static final String TEMPLATE_MGT_NORECORD_FOUND = "template.norecord.found";
    public static final String TEMPLATE_MGT_CONFIRM_SUCCESSFULLY = "template.confirm.success";
    public static final String TEMPLATE_MGT_REJECT_SUCCESSFULLY = "template.reject.success";
    public static final String TEMPLATE_MGT_ALREADY_EXISTS = "template.already.exists";
    public static final String TEMPLATE_MGT_EMPTY_CODE = "template.empty.categoryCode";
    public static final String TEMPLATE_MGT_EMPTY_DESCRIPTION = "template.empty.description";
    public static final String TEMPLATE_MGT_EMPTY_STATUS = "template.empty.status";
    public static final String TEMPLATE_MGT_EMPTY_MESSAGE_FORMAT = "template.empty.message.format";
    public static final String SMS_TEMPLATE_INVALID_LENGTH_SMS_FORMAT = "template.invalid.length.message.format";
    //-------------------------- template mgt-------------------------------------------------------------------------//

    //-------------------------- smpp configuration mgt---------------------------------------------------------------//
    public static final String SMPP_CONFIGURATION_MGT_ADDED_SUCCESSFULLY = "smppconfiguration.added.success";
    public static final String SMPP_CONFIGURATION_MGT_UPDATE_SUCCESSFULLY = "smppconfiguration.updated.success";
    public static final String SMPP_CONFIGURATION_MGT_DELETE_SUCCESSFULLY = "smppconfiguration.deleted.success";
    public static final String SMPP_CONFIGURATION_MGT_CONFIRM_SUCCESSFULLY = "smppconfiguration.confirm.success";
    public static final String SMPP_CONFIGURATION_MGT_REJECT_SUCCESSFULLY = "smppconfiguration.reject.success";
    public static final String SMPP_CONFIGURATION_MGT_ALREADY_EXISTS = "smppconfiguration.already.exists";
    public static final String SMPP_CONFIGURATION_MGT_NORECORD_FOUND = "smppconfiguration.norecord.found";
    public static final String SMPP_CONFIGURATION_MGT_EMPTY_SMPPCODE = "smppconfiguration.empty.smppCode";
    public static final String SMPP_CONFIGURATION_MGT_EMPTY_DESCRIPTION = "smppconfiguration.empty.description";
    public static final String SMPP_CONFIGURATION_MGT_EMPTY_STATUS = "smppconfiguration.empty.status";
    public static final String SMPP_CONFIGURATION_MGT_EMPTY_MAXTPS = "smppconfiguration.empty.maxTps";
    public static final String SMPP_CONFIGURATION_MGT_EMPTY_PRIMARYIP = "smppconfiguration.empty.primaryIp";
    public static final String SMPP_CONFIGURATION_MGT_EMPTY_SECONDARYIP = "smppconfiguration.empty.secondaryIp";
    public static final String SMPP_CONFIGURATION_MGT_EMPTY_SYSTEMID = "smppconfiguration.empty.systemId";
    public static final String SMPP_CONFIGURATION_MGT_EMPTY_PASSWORD = "smppconfiguration.empty.password";
    public static final String SMPP_CONFIGURATION_MGT_EMPTY_BINDPORT = "smppconfiguration.empty.bindPort";
    public static final String SMPP_CONFIGURATION_MGT_EMPTY_BINDMODE = "smppconfiguration.empty.bindMode";
    public static final String SMPP_CONFIGURATION_MGT_EMPTY_MTPORT = "smppconfiguration.empty.mtPort";
    public static final String SMPP_CONFIGURATION_MGT_EMPTY_MOPORT = "smppconfiguration.empty.moPort";
    public static final String SMPP_CONFIGURATION_MGT_EMPTY_MAXBULKTPS = "smppconfiguration.empty.maxBulkTps";

    //-------------------------- smpp configuration mgt---------------------------------------------------------------//

    //-------------------------- section mgt-----------------------------------------------------------------------//
    public static final String SECTION_MGT_ADDED_SUCCESSFULLY = "section.added.success";
    public static final String SECTION_MGT_UPDATE_SUCCESSFULLY = "section.updated.success";
    public static final String SECTION_MGT_DELETE_SUCCESSFULLY = "section.deleted.success";
    public static final String SECTION_MGT_CONFIRM_SUCCESSFULLY = "section.confirm.success";
    public static final String SECTION_MGT_REJECT_SUCCESSFULLY = "section.reject.success";
    public static final String SECTION_MGT_ALREADY_EXISTS = "section.already.exists";
    public static final String SECTION_MGT_NORECORD_FOUND = "section.norecord.found";
    public static final String SECTION_MGT_EMPTY_SECTIONCODE = "section.empty.sectionCode";
    public static final String SECTION_MGT_EMPTY_DESCRIPTION = "section.empty.description";
    public static final String SECTION_MGT_EMPTY_STATUS = "section.empty.status";
    public static final String SECTION_MGT_EMPTY_SORTKEY = "section.empty.sortKey";
    //-------------------------- section mgt-----------------------------------------------------------------------//

    //-------------------------- customer mgt-----------------------------------------------------------------------//
    public static final String CUSTOMER_MGT_CONFIRM_SUCCESSFULLY = "customer.confirm.success";
    public static final String CUSTOMER_MGT_REJECT_SUCCESSFULLY = "customer.reject.success";
    public static final String CUSTOMER_MGT_UPDATE_SUCCESSFULLY = "customer.updated.success";
    public static final String CUSTOMER_MGT_NORECORD_FOUND = "customer.norecord.found";
    public static final String CUSTOMER_MGT_EMPTY_STATUS = "customer.empty.status";
    public static final String CUSTOMER_MGT_EMPTY_WAIVE_OFF_STATUS = "customer.empty.waiveoffstatus";
    //-------------------------- customer mgt-----------------------------------------------------------------------//

    //-------------------------- channel mgt-----------------------------------------------------------------------//
    public static final String CHANNEL_MGT_ADDED_SUCCESSFULLY = "channel.added.success";
    public static final String CHANNEL_MGT_UPDATE_SUCCESSFULLY = "channel.updated.success";
    public static final String CHANNEL_MGT_DELETE_SUCCESSFULLY = "channel.deleted.success";
    public static final String CHANNEL_MGT_CONFIRM_SUCCESSFULLY = "channel.confirm.success";
    public static final String CHANNEL_MGT_REJECT_SUCCESSFULLY = "channel.reject.success";
    public static final String CHANNEL_MGT_ALREADY_EXISTS = "channel.already.exists";
    public static final String CHANNEL_MGT_NORECORD_FOUND = "channel.norecord.found";
    public static final String CHANNEL_MGT_EMPTY_CHANNELCODE = "channel.empty.channelCode";
    public static final String CHANNEL_MGT_EMPTY_DESCRIPTION = "channel.empty.description";
    public static final String CHANNEL_MGT_EMPTY_STATUS = "channel.empty.status";
    //-------------------------- channel mgt-----------------------------------------------------------------------//

    //-------------------------- supplier mgt-----------------------------------------------------------------------//
    public static final String SUPPLIER_MGT_ADDED_SUCCESSFULLY = "supplier.added.success";
    public static final String SUPPLIER_MGT_UPDATE_SUCCESSFULLY = "supplier.updated.success";
    public static final String SUPPLIER_MGT_DELETE_SUCCESSFULLY = "supplier.deleted.success";
    public static final String SUPPLIER_MGT_CONFIRM_SUCCESSFULLY = "supplier.confirm.success";
    public static final String SUPPLIER_MGT_REJECT_SUCCESSFULLY = "supplier.reject.success";
    public static final String SUPPLIER_MGT_ALREADY_EXISTS = "supplier.already.exists";
    public static final String SUPPLIER_MGT_NORECORD_FOUND = "supplier.norecord.found";
    public static final String SUPPLIER_MGT_EMPTY_SECTIONCODE = "supplier.empty.sectionCode";
    public static final String SUPPLIER_MGT_EMPTY_DESCRIPTION = "supplier.empty.description";
    public static final String SUPPLIER_MGT_EMPTY_STATUS = "supplier.empty.status";
    public static final String SUPPLIER_MGT_EMPTY_SORTKEY = "supplier.empty.sortKey";
    //-------------------------- supplier mgt-----------------------------------------------------------------------//

    //-------------------------- dealership mgt-----------------------------------------------------------------------//
    public static final String DEALERSHIP_MGT_ADDED_SUCCESSFULLY = "dealership.added.success";
    public static final String DEALERSHIP_MGT_UPDATE_SUCCESSFULLY = "dealership.updated.success";
    public static final String DEALERSHIP_MGT_DELETE_SUCCESSFULLY = "dealership.deleted.success";
    public static final String DEALERSHIP_MGT_CONFIRM_SUCCESSFULLY = "dealership.confirm.success";
    public static final String DEALERSHIP_MGT_REJECT_SUCCESSFULLY = "dealership.reject.success";
    public static final String DEALERSHIP_MGT_ALREADY_EXISTS = "dealership.already.exists";
    public static final String DEALERSHIP_MGT_NO_RECORD_FOUND = "dealership.norecord.found";
    public static final String DEALERSHIP_MGT_EMPTY_SECTIONCODE = "dealership.empty.sectionCode";
    public static final String DEALERSHIP_MGT_EMPTY_DESCRIPTION = "dealership.empty.description";
    public static final String DEALERSHIP_MGT_EMPTY_STATUS = "dealership.empty.status";
    public static final String DEALERSHIP_MGT_EMPTY_SORTKEY = "dealership.empty.sortKey";
    //-------------------------- dealership mgt-----------------------------------------------------------------------//
}
