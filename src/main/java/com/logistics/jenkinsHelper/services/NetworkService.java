package com.logistics.jenkinsHelper.services;

import com.logistics.jenkinsHelper.exceptions.InvalidInputException;
import com.logistics.jenkinsHelper.util.InputParser;
import com.logistics.jenkinsHelper.util.Params;
import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.management.dns.ARecordSet;
import com.microsoft.azure.management.dns.DnsZone;
import com.microsoft.azure.management.dns.implementation.DnsZoneManager;

import java.util.List;
import java.util.stream.Collectors;

public class NetworkService {

    public void turnGreen(String environment, String subscription, String client, String secret, String tenant, String resourceGroupName, String dnsZoneName, String aname, String ipAddress, String ttl) throws InvalidInputException{
        checkInputParams(environment, subscription, client, secret, tenant, resourceGroupName, dnsZoneName, aname, ipAddress, ttl);

        ApplicationTokenCredentials credentials = new ApplicationTokenCredentials(client,
                tenant,
                secret,
                AzureEnvironment.AZURE);

        DnsZoneManager zoneManager = authenticate(subscription, credentials);
        DnsZone zone = getDNSZone(resourceGroupName, dnsZoneName, zoneManager);

        // TODO: change blue/green process for the Firewall to handle it
        removeOldRecordSet(zone, getAllCurrentRecordSets(zone), aname);

        applyNewARecord(zone, aname, ipAddress, ttl);
    }

    private void removeOldRecordSet(DnsZone zone, List<ARecordSet> oldRecords, String environment) {
        oldRecords.stream().filter(aRecordSet -> aRecordSet.name().equals(environment))
                            .forEach(aRecordSet -> zone.update()
                                                .withoutARecordSet(aRecordSet.name())
                                                .apply());
    }

    private DnsZone getDNSZone(String resourceGroupName, String dnsZoneName, DnsZoneManager zoneManager) {
        return zoneManager.zones().getByResourceGroup(resourceGroupName, dnsZoneName);
    }

    private void applyNewARecord(DnsZone zone, String aname, String ipAddress, String ttl) {
        zone.update()
            .defineARecordSet(aname)
            .withIPv4Address(ipAddress)
            .withTimeToLive(Long.parseLong(ttl))
            .attach()
            .apply();
    }

    private DnsZoneManager authenticate(String subscription, ApplicationTokenCredentials credentials) {
        return DnsZoneManager.authenticate(credentials, subscription);
    }

    private List<ARecordSet> getAllCurrentRecordSets(DnsZone zone) {
        PagedList<ARecordSet> oldARecords = zone.aRecordSets().list();
        oldARecords.loadAll();
        return oldARecords.stream().collect(Collectors.toList());
    }

    private void checkInputParams(String environment, String subscription, String client, String secret, String tenant, String resourceGroupName, String dnsZoneName, String aname, String ipAddress, String ttl) throws InvalidInputException{
        InputParser.checkValidEnvironment(environment, Params.ENVIRONMENT);
        InputParser.checkEmptyInput(subscription, Params.SUBSCRIPTION_ID);
        InputParser.checkEmptyInput(client, Params.CLIENT_ID);
        InputParser.checkEmptyInput(secret, Params.SECRET_KEY);
        InputParser.checkEmptyInput(tenant, Params.TENANT_ID);
        InputParser.checkEmptyInput(resourceGroupName, Params.RG_NAME);
        InputParser.checkEmptyInput(dnsZoneName, Params.DNS_ZONE_NAME);
        InputParser.checkEmptyInput(aname, Params.ANAME);
        InputParser.checkEmptyInput(ipAddress, Params.IPADDRESS);
        InputParser.checkEmptyInput(ttl, Params.TTL);
        checkValidLongValue(ttl, Params.TTL);
    }

    private void checkValidLongValue(String value, String variableNameIfError) throws InvalidInputException{
        try{
            Long.parseLong(value);
        }catch(Exception e){
            throw new InvalidInputException("Invalid number value for "+variableNameIfError);
        }
    }

}
