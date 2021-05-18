# Jenkins Helper

This is part of the big project available at: http://sesterheim.com.br/hands-on-implementing-blue-green-deployment-on-azure-with-aks/

Available commands:

* Pull Next File Name for Terraform to destroy

<code>java -jar jenkins-helper-1.0-RELEASE.jar connection_string="\<conn_string\>" oper="pull_next_file" container="\<container-name\>" file="\<file name\>"</code>

* Check if there is a single environment up

<code>java -jar jenkins-helper-1.0-RELEASE.jar connection_string="\<conn_string\>" oper="is_single_env_up" container="\<container-name\>" file="\<file-name\>"</code>

* Turn new IP into green environment

<code>java -jar jenkins-helper-1.0-RELEASE.jar oper="turn_green" subscription_id="\<subscription_id\>" client_id="\<client_id\>" secret_key="\<secret key\>" tenant_id="\<tenant_id\>" resource_group_name="\<RG name\>" dns_zone_name="\<dns zone name\>" a_name="\<A name\>" ip_address="\<ip address\>" ttl="\<ttl\>"</code>