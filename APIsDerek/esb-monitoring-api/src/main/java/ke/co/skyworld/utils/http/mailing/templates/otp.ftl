<#import "fragments/layout.ftl" as l>
<@l.layout>
    <p style="font-family: 'Noto Sans', sans-serif; font-size:14px; line-height: 22px; font-weight:500; text-align: left; color:#464646;">
        <b>Dear ${model.fullName},</b>
        <br />
        Your Sky Portal One Time Password (OTP) is:
        <br /><br />
        <span style="font-family: Consolas, Menlo, Monaco, Lucida Console, Liberation Mono, DejaVu Sans Mono, Bitstream Vera Sans Mono, Courier New, monospace, serif;
                              font-size:20px; text-align: left;
                              color:#464646; font-weight: bold; padding-top: 10px; padding-bottom: 10px;
                              padding-left: 20px; padding-right: 20px; background-color: rgba(21,101,192,0.27)">
                     ${model.otp}
        </span>
        <br /><br />
        Generated at <span style="font-weight: bold">${model.otpDateCreated}</span>. Valid till <span style="font-weight: bold">${model.otpExpiryDate}</span>.
    </p>
    <p style="padding-top: 10px; font-family: 'Noto Sans', sans-serif; font-size:14px; line-height: 22px; font-weight:500; text-align: left; color:#464646;">
        Log into <a href="${model.portalLoginLink}">Sky World Portal</a>
    </p>
</@l.layout>