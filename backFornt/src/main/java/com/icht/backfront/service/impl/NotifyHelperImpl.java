package com.icht.backfront.service.impl;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.icht.backfront.model.Notify;
import com.icht.backfront.service.NotifyHelper;
import com.taobao.api.ApiException;
import org.springframework.stereotype.Service;

@Service
public class NotifyHelperImpl implements NotifyHelper {
    @Override
    public void sendNotify(Notify notify) {
        switch (notify.getNotifyType()){
            case "text":
            {
                DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/robot/send?access_token=9afa6131621cfe3d9df1c4ca8fb09ba964a11667c0b5ee08bf67b7fef9587c01");

                OapiRobotSendRequest request=new OapiRobotSendRequest();

                request.setMsgtype("text");
                request.setMsgtype("");

                OapiRobotSendRequest.Text text=new OapiRobotSendRequest.Text();
                text.setContent(notify.getText());
                request.setText(text);

                OapiRobotSendRequest.At at=new OapiRobotSendRequest.At();
                at.setIsAtAll(true);
                request.setAt(at);
                try {
                    client.execute(request);
                } catch (ApiException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "link":
            {
                DingTalkClient client=new DefaultDingTalkClient("https://oapi.dingtalk.com/robot/send?access_token=9afa6131621cfe3d9df1c4ca8fb09ba964a11667c0b5ee08bf67b7fef9587c01");

                OapiRobotSendRequest request=new OapiRobotSendRequest();
                request.setMsgtype("link");
                OapiRobotSendRequest.Link link=new OapiRobotSendRequest.Link();
                link.setMessageUrl(notify.getLink());
                link.setPicUrl(notify.getPciUrl());
                link.setTitle(notify.getTitle());
                link.setText(notify.getText());
                request.setLink(link);
                try {
                    client.execute(request);
                } catch (ApiException e) {
                    e.printStackTrace();
                }
                break;
            }
            case "markdown":
            {
                DingTalkClient client=new DefaultDingTalkClient("https://oapi.dingtalk.com/robot/send?access_token=9afa6131621cfe3d9df1c4ca8fb09ba964a11667c0b5ee08bf67b7fef9587c01");

                OapiRobotSendRequest request=new OapiRobotSendRequest();
                request.setMsgtype("markdown");
                OapiRobotSendRequest.Markdown markdown=new OapiRobotSendRequest.Markdown();

                markdown.setTitle(notify.getTitle());
                markdown.setText(notify.getText());

                request.setMarkdown(markdown);
                try {
                    client.execute(request);
                } catch (ApiException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
