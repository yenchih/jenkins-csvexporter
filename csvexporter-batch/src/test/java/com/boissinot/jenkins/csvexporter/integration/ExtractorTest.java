package com.boissinot.jenkins.csvexporter.integration;

import com.boissinot.jenkins.csvexporter.domain.InputSBJobObj;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.Message;
import org.springframework.integration.MessageHeaders;
import org.springframework.integration.MessagingException;
import org.springframework.integration.core.MessageHandler;
import org.springframework.integration.core.SubscribableChannel;
import org.springframework.integration.support.MessageBuilder;

/**
 * @author Gregory Boissinot
 */
public class ExtractorTest {

    @Test
    @Ignore
    public void test() {

        String configXML = "<?xml version='1.0' encoding='UTF-8'?><maven2-moduleset>  <actions/>  <description>Master project description for all projects from Soleil Synchrotron</description>  <logRotator>    <daysToKeep>7</daysToKeep>    <numToKeep>10</numToKeep>    <artifactDaysToKeep>-1</artifactDaysToKeep>    <artifactNumToKeep>-1</artifactNumToKeep>  </logRotator>  <keepDependencies>false</keepDependencies>  <properties>    <hudson.plugins.descriptionsetter.JobByDescription/>    <hudson.plugins.disk__usage.DiskUsageProperty/>    <hudson.plugins.mantis.MantisProjectProperty>      <siteName>http://controle.synchrotron-soleil.fr/mantis/</siteName>      <regexpPattern>        <pattern>(?&lt;=issue #?)(\\d+)(?=)</pattern>        <flags>0</flags>      </regexpPattern>      <linkEnabled>false</linkEnabled>    </hudson.plugins.mantis.MantisProjectProperty>    <hudson.plugins.batch__task.BatchTaskProperty>      <tasks>        <hudson.plugins.batch__task.BatchTask>          <name>release rollback</name>          <script>mvn release:rollback</script>        </hudson.plugins.batch__task.BatchTask>      </tasks>    </hudson.plugins.batch__task.BatchTaskProperty>  </properties>  <scm class=\"hudson.scm.CVSSCM\">    <cvsroot>:ext:maven@ganymede.synchrotron-soleil.fr:/usr/local/CVS</cvsroot>    <module>ContinuousIntegration/maven/packaging/OmniRoot-WIN32</module>    <canUseUpdate>true</canUseUpdate>    <flatten>true</flatten>    <isTag>false</isTag>    <excludedRegions></excludedRegions>  </scm>  <assignedNode>windows</assignedNode>  <canRoam>false</canRoam>  <disabled>false</disabled>  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>  <triggers class=\"vector\"/>  <concurrentBuild>false</concurrentBuild>  <rootModule>    <groupId>fr.soleil.packaging</groupId>    <artifactId>OmniRoot-WIN32</artifactId>  </rootModule>  <goals>clean -Pstable</goals>  <mavenOpts>-Xmx256m</mavenOpts>  <aggregatorStyleBuild>true</aggregatorStyleBuild>  <incrementalBuild>true</incrementalBuild>  <usePrivateRepository>true</usePrivateRepository>  <ignoreUpstremChanges>true</ignoreUpstremChanges>  <archivingDisabled>false</archivingDisabled>  <resolveDependencies>false</resolveDependencies>  <processPlugins>false</processPlugins>  <mavenValidationLevel>0</mavenValidationLevel>  <reporters/>  <publishers>    <hudson.plugins.dependencyanalyzer.DependencyAnalyzerPublisher/>  </publishers>  <buildWrappers>    <org.jvnet.hudson.plugins.m2release.M2ReleaseBuildWrapper>      <releaseGoals>-DenableCheckRelease=false -Drelease=true -Pstable release:clean release:prepare  release:perform</releaseGoals>      <defaultVersioningMode>specify_version</defaultVersioningMode>      <selectCustomScmCommentPrefix>false</selectCustomScmCommentPrefix>      <selectAppendHudsonUsername>true</selectAppendHudsonUsername>    </org.jvnet.hudson.plugins.m2release.M2ReleaseBuildWrapper>    <hudson.plugins.m2extrasteps.M2ExtraStepsWrapper>      <preBuildSteps/>      <postBuildSteps>        <hudson.tasks.Maven>          <targets>versions:update-parent</targets>          <mavenName>mvn</mavenName>          <usePrivateRepository>true</usePrivateRepository>        </hudson.tasks.Maven>        <hudson.tasks.Maven>          <targets>versions:update-properties -Punstable  -DallowSnapshots</targets>          <mavenName>mvn</mavenName>          <usePrivateRepository>true</usePrivateRepository>        </hudson.tasks.Maven>        <hudson.tasks.Maven>          <targets>versions:update-properties -Pstable</targets>          <mavenName>mvn</mavenName>          <usePrivateRepository>true</usePrivateRepository>        </hudson.tasks.Maven>        <hudson.tasks.Maven>          <targets>versions:commit</targets>          <mavenName>mvn</mavenName>          <usePrivateRepository>true</usePrivateRepository>        </hudson.tasks.Maven>        <hudson.tasks.Maven>          <targets>-Dmessage=&quot;Update release from Hudson&quot; scm:checkin</targets>          <mavenName>mvn</mavenName>          <usePrivateRepository>true</usePrivateRepository>        </hudson.tasks.Maven>        <hudson.tasks.Maven>          <targets>deploy</targets>          <mavenName>mvn</mavenName>          <usePrivateRepository>true</usePrivateRepository>        </hudson.tasks.Maven>      </postBuildSteps>      <runIfResult>allCases</runIfResult>    </hudson.plugins.m2extrasteps.M2ExtraStepsWrapper>  </buildWrappers></maven2-moduleset>";
        InputSBJobObj inputSBJobObj = new InputSBJobObj(
                "testJob",
                "JAVA",
                "BUILD", configXML, null);


        ApplicationContext applicationContext
                = new ClassPathXmlApplicationContext("applicationContext-csv.xml");

        SubscribableChannel jobs = applicationContext.getBean("jobs", SubscribableChannel.class);

        SubscribableChannel csvJobs = applicationContext.getBean("csvJobs", SubscribableChannel.class);


        csvJobs.subscribe(new MessageHandler() {
            public void handleMessage(Message<?> message) throws MessagingException {
                System.out.println(message);
            }
        });

        jobs.send(MessageBuilder
                .withPayload(inputSBJobObj)
                .setHeader(MessageHeaders.REPLY_CHANNEL, csvJobs)
                .build());
    }
}
