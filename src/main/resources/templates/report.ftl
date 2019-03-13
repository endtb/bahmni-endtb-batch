<html>
<head>
    <title>Reports</title>
    <meta charset="utf-8">
    <meta name="HandheldFriendly" content="true">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=2.0, user-scalable=yes, minimal-ui" />
    <meta name="apple-mobile-web-app-capable" content="yes" />
    <link rel="shortcut icon" type="image/x-icon" href="../../bahmni/favicon.ico">
    <link rel="stylesheet" href="../../bahmni_config/openmrs/apps/exports/css/jobResult.css">
</head>

<body>
<div class="opd-header-wrapper">
    <div class="opd-header-top">
        <header>
            <ul>
                <li ng-repeat="backLink in backLinks">
                    <a class="back-btn" accesskey="h" href="../../bahmni/home/">
                        <i class="fa fa-home"></i>
                    </a>
                </li>
            </ul>
        </header>
    </div>
</div>
<div class="opd-wrapper bahmni-reports">
    <div class="opd-content-wrapper">
        <section class="section-grid">
            <h2 class="section-title">Endtb Exports</h2>
            <table class="alt-row">
                <thead>
                <tr>
                    <th>Date</th>
                    <th>Job Name</th>
                    <th>Job Status</th>
                    <th>Download</th>
                </tr>
                </thead>
                <tbody>
                <#list input as jobResult><tr>
                    <td>${jobResult.dateOfExecution}</td>
                    <td>${jobResult.jobName}</td>
                    <td>${jobResult.status}</td>
                    <#if jobResult.status == 'COMPLETED'>
                        <td><a href="/openmrs/ws/rest/v1/endtb/export?filename=${jobResult.zipFileName}" class="button small report-download"><i class="fa fa-download"></i></a></td>
                    </#if>
                </tr>
                </#list>
                </tbody>
            </table>
        </section>
    </div>
</div>
</body>
</html>
