import groovy.json.JsonOutput

def readCommitAuthor() {
    sh '''#!/bin/bash
        git rev-parse HEAD | tr '\n' ' ' > gitCommit
        git show --format="%aN <%aE>" ${gitCommit} | head -1 | tr '\n' ' ' > gitCommitAuthor
    '''
    return readFile('gitCommitAuthor')
}

def durationTime(m1, m2) {
    int timecase = m2 - m1

    int seconds = (int) (timecase / 1000)
    int minutes = (int) (timecase / (60*1000))
    int hours = (int) (timecase / (1000*60*60))

    return hours.mod(24) + "h " + minutes.mod(60) + "m " + seconds.mod(60) + "s"
}

def findPodsFromName(String namespace, String name) {
    def podsAndImagesRaw = sh(
        script: "kubectl get pods -n ${namespace} --selector=app=${name} -o jsonpath='{range .items[*]}{.metadata.name}###'",
        returnStdout: true
    ).trim()

    if (!podsAndImagesRaw) {
        return []
    }

    def wantedPods = podsAndImagesRaw.split('###')
    return wantedPods.findAll { it?.trim() } // removes empty strings
}


// def notifySlack(slackURL, text, channel, attachments) {
//     def jenkinsIcon = 'https://a.slack-edge.com/205a/img/services/jenkins-ci_72.png'

//     def payload = groovy.json.JsonOutput.toJson([
//         text: text,
//         channel: channel,
//         username: "jenkins",
//         icon_url: jenkinsIcon,
//         attachments: attachments
//     ])

//     // Run curl safely without leaking secrets
//     sh '''
//       curl -s -X POST "$SLACK_URL" \
//         -H "Cache-Control: no-cache" \
//         -H "Content-Type: application/json;charset=UTF-8" \
//         -d @payload.json
//     '''
// }
return this