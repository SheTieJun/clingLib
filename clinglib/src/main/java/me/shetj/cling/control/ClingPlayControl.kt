package me.shetj.cling.control;

import android.util.Log;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.avtransport.callback.GetPositionInfo;
import org.fourthline.cling.support.avtransport.callback.Pause;
import org.fourthline.cling.support.avtransport.callback.Play;
import org.fourthline.cling.support.avtransport.callback.Seek;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;
import org.fourthline.cling.support.avtransport.callback.Stop;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.AudioItem;
import org.fourthline.cling.support.model.item.ImageItem;
import org.fourthline.cling.support.model.item.VideoItem;
import org.fourthline.cling.support.renderingcontrol.callback.GetVolume;
import org.fourthline.cling.support.renderingcontrol.callback.SetMute;
import org.fourthline.cling.support.renderingcontrol.callback.SetVolume;
import org.jetbrains.annotations.NotNull;
import org.seamless.util.MimeType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.shetj.cling.callback.ControlCallback;
import me.shetj.cling.callback.ControlReceiveCallback;
import me.shetj.cling.entity.ClingPlayState;
import me.shetj.cling.entity.ClingPlayType;
import me.shetj.cling.manager.ClingManager;
import me.shetj.cling.util.ClingUtils;
import me.shetj.cling.util.Utils;


public class ClingPlayControl implements IPlayControl {
    private static final String TAG = ClingPlayControl.class.getSimpleName();
    /** 每次接收 500ms 延迟 */
    private static final int RECEIVE_DELAY = 500;
    /** 上次设置音量时间戳, 防抖动 */
    private long mVolumeLastTime;
    /**
     * 当前状态
     */
    private ClingPlayState mCurrentState = ClingPlayState.STOP;
    private static final String DIDL_LITE_FOOTER = "</DIDL-Lite>";
    private static final String DIDL_LITE_HEADER = "<?xml version=\"1.0\"?>" + "<DIDL-Lite " + "xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\" " +
            "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" " + "xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" " +
            "xmlns:dlna=\"urn:schemas-dlna-org:metadata-1-0/\">";

    @Override
    public void playNew(final String url, final ClingPlayType ItemType, final ControlCallback callback) {

        stop(new ControlCallback() { // 1、 停止当前播放视频
            @Override
            public void success(Object response) {

                setAVTransportURI(url,ItemType, new ControlCallback() {
                    @Override
                    public void fail(@NotNull Exception response) {
                        if (Utils.isNotNull(callback)) {
                            callback.fail(response);
                        }
                    }

                    @Override
                    public void success(Object response) {
                        play(callback);                        // 3、播放视频
                    }
                });
            }

            @Override
            public void fail(@NotNull Exception response) {
                if (Utils.isNotNull(callback)) {
                    callback.fail(response);
                }
            }
        });
    }

    @Override
    public void play(final ControlCallback callback) {
        final Service avtService = ClingUtils.findServiceFromSelectedDevice(ClingManager.AV_TRANSPORT_SERVICE);
        if (Utils.isNull(avtService)) {
            return;
        }

        final ControlPoint controlPointImpl = ClingUtils.getControlPoint();
        if (Utils.isNull(controlPointImpl)) {
            return;
        }

        controlPointImpl.execute(new Play(avtService) {

            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                if (Utils.isNotNull(callback)) {
                    callback.success(invocation);
                }
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if (Utils.isNotNull(callback)) {
                    callback.fail(new Exception(defaultMsg));
                }
            }
        });
    }

    @Override
    public void pause(final ControlCallback callback) {
        final Service avtService = ClingUtils.findServiceFromSelectedDevice(ClingManager.AV_TRANSPORT_SERVICE);
        if (Utils.isNull(avtService)) {
            return;
        }

        final ControlPoint controlPointImpl = ClingUtils.getControlPoint();
        if (Utils.isNull(controlPointImpl)) {
            return;
        }

        controlPointImpl.execute(new Pause(avtService) {

            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                if (Utils.isNotNull(callback)) {
                    callback.success(invocation);
                }
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if (Utils.isNotNull(callback)) {
                    callback.fail(new Exception(defaultMsg));
                }
            }
        });
    }

    @Override
    public void stop(final ControlCallback callback) {
        final Service avtService = ClingUtils.findServiceFromSelectedDevice(ClingManager.AV_TRANSPORT_SERVICE);
        if (Utils.isNull(avtService)) {
            return;
        }

        final ControlPoint controlPointImpl = ClingUtils.getControlPoint();
        if (Utils.isNull(controlPointImpl)) {
            return;
        }

        controlPointImpl.execute(new Stop(avtService) {

            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                if (Utils.isNotNull(callback)) {
                    callback.success(invocation);
                }
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if (Utils.isNotNull(callback)) {
                    callback.fail(new Exception(defaultMsg));
                }
            }
        });
    }

    @Override
    public void seek(int pos, final ControlCallback callback) {
        final Service avtService = ClingUtils.findServiceFromSelectedDevice(ClingManager.AV_TRANSPORT_SERVICE);
        if (Utils.isNull(avtService)) {
            return;
        }

        final ControlPoint controlPointImpl = ClingUtils.getControlPoint();
        if (Utils.isNull(controlPointImpl)) {
            return;
        }

        String time = Utils.getStringTime(pos);
        Log.e(TAG, "seek->pos: " + pos + ", time: " + time);
        controlPointImpl.execute(new Seek(avtService, time) {

            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                if (Utils.isNotNull(callback)) {
                    callback.success(invocation);
                }
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if (Utils.isNotNull(callback)) {
                    callback.fail(new Exception(defaultMsg));
                }
            }
        });
    }

    @Override
    public void setVolume(int pos,  final ControlCallback callback) {
        final Service rcService = ClingUtils.findServiceFromSelectedDevice(ClingManager.RENDERING_CONTROL_SERVICE);
        if (Utils.isNull(rcService)) {
            return;
        }

        final ControlPoint controlPointImpl = ClingUtils.getControlPoint();
        if (Utils.isNull(controlPointImpl)) {
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis > mVolumeLastTime + RECEIVE_DELAY){
            controlPointImpl.execute(new SetVolume(rcService, pos) {

                @Override
                public void success(ActionInvocation invocation) {
                    if (Utils.isNotNull(callback)) {
                        callback.success(invocation);
                    }
                }

                @Override
                public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                    if (Utils.isNotNull(callback)) {
                        callback.fail(new Exception(defaultMsg));
                    }
                }
            });
        }
        mVolumeLastTime = currentTimeMillis;
    }

    @Override
    public void setMute(boolean desiredMute,  final ControlCallback callback) {
        final Service rcService = ClingUtils.findServiceFromSelectedDevice(ClingManager.RENDERING_CONTROL_SERVICE);
        if (Utils.isNull(rcService)) {
            return;
        }

        final ControlPoint controlPointImpl = ClingUtils.getControlPoint();
        if (Utils.isNull(controlPointImpl)) {
            return;
        }

        controlPointImpl.execute(new SetMute(rcService, desiredMute) {

            @Override
            public void success(ActionInvocation invocation) {
                if (Utils.isNotNull(callback)) {
                    callback.success(invocation);
                }
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if (Utils.isNotNull(callback)) {
                    callback.fail(new Exception(defaultMsg));
                }
            }
        });
    }

    /**
     * 设置片源，用于首次播放
     *
     * @param url   片源地址
     * @param callback  回调
     */
    private void setAVTransportURI(String url,  ClingPlayType ItemType,final ControlCallback callback) {
        if (Utils.isNull(url)) {
            return;
        }

        String metadata = pushMediaToRender(url, "id", "name",ItemType );

        final Service avtService = ClingUtils.findServiceFromSelectedDevice(ClingManager.AV_TRANSPORT_SERVICE);
        if (Utils.isNull(avtService)) {
            return;
        }

        final ControlPoint controlPointImpl = ClingUtils.getControlPoint();
        if (Utils.isNull(controlPointImpl)) {
            return;
        }

        controlPointImpl.execute(new SetAVTransportURI(avtService, url, metadata) {

            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                if (Utils.isNotNull(callback)) {
                    callback.success(invocation);
                }
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if (Utils.isNotNull(callback)) {
                    callback.fail(new Exception(defaultMsg));
                }
            }
        });
    }

    @Override
    public void getPositionInfo(final ControlReceiveCallback callback) {

        final Service avtService = ClingUtils.findServiceFromSelectedDevice(ClingManager.AV_TRANSPORT_SERVICE);
        if (Utils.isNull(avtService)) {
            return;
        }

        Log.d(TAG, "Found media render service in device, sending get position");

        GetPositionInfo getPositionInfo = new GetPositionInfo(avtService) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if (Utils.isNotNull(callback)) {
                    callback.fail(new Exception(defaultMsg));
                }
            }

            @Override
            public void success(ActionInvocation invocation) {
                super.success(invocation);
                if (Utils.isNotNull(callback)) {
                    callback.success(invocation);
                }
            }

            @Override
            public void received(ActionInvocation invocation, PositionInfo info) {
                if (Utils.isNotNull(callback)) {
                    callback.receive(info.getTrackElapsedSeconds());
                }
            }
        };

        ControlPoint controlPointImpl = ClingUtils.getControlPoint();
        if (Utils.isNull(controlPointImpl)) {
            return;
        }

        controlPointImpl.execute(getPositionInfo);
    }

    @Override
    public void getVolume(final ControlReceiveCallback callback) {
        final Service avtService = ClingUtils.findServiceFromSelectedDevice(ClingManager.RENDERING_CONTROL_SERVICE);
        if (Utils.isNull(avtService)) {
            return;
        }
        GetVolume getVolume = new GetVolume(avtService) {
            @Override
            public void received(ActionInvocation actionInvocation, int currentVolume) {
                if (Utils.isNotNull(callback)) {
                    callback.receive(currentVolume);
                }
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                if (Utils.isNotNull(callback)) {
                    callback.fail(new Exception(defaultMsg));
                }
            }
        };

        ControlPoint controlPointImpl = ClingUtils.getControlPoint();
        if (Utils.isNull(controlPointImpl)) {
            return;
        }

        controlPointImpl.execute(getVolume);
    }

    public  ClingPlayState getCurrentState() {
        return mCurrentState;
    }

    public void setCurrentState(ClingPlayState currentState) {
        if (this.mCurrentState != currentState) {
            this.mCurrentState = currentState;
        }
    }


    private String pushMediaToRender(String url, String id, String name, ClingPlayType ItemType) {
        final long size = 0;
        final Res res = new Res(new MimeType(ProtocolInfo.WILDCARD, ProtocolInfo.WILDCARD), size, url);
        final String creator = "unknow";
        final String parentId = "0";
        final String metadata;
        switch (ItemType) {
            case  TYPE_IMAGE:
                ImageItem imageItem = new ImageItem(id, parentId, name, creator, res);
                metadata = createItemMetadata(imageItem);
                break;
            case TYPE_VIDEO:
                VideoItem videoItem = new VideoItem(id, parentId, name, creator, res);
                metadata = createItemMetadata(videoItem);
                break;
            case TYPE_AUDIO:
                AudioItem audioItem = new AudioItem(id, parentId, name, creator, res);
                metadata = createItemMetadata(audioItem);
                break;
            default:
                throw new IllegalArgumentException("UNKNOWN MEDIA TYPE");
        }
        return metadata;
    }

    /**
     * 创建投屏的参数
     *
     * @param item
     * @return
     */
    private String createItemMetadata(DIDLObject item) {
        StringBuilder metadata = new StringBuilder();
        metadata.append(DIDL_LITE_HEADER);

        metadata.append(String.format("<item id=\"%s\" parentID=\"%s\" restricted=\"%s\">", item.getId(), item.getParentID(), item.isRestricted() ? "1" : "0"));

        metadata.append(String.format("<dc:title>%s</dc:title>", item.getTitle()));
        String creator = item.getCreator();
        if (creator != null) {
            creator = creator.replaceAll("<", "_");
            creator = creator.replaceAll(">", "_");
        }
        metadata.append(String.format("<upnp:artist>%s</upnp:artist>", creator));
        metadata.append(String.format("<upnp:class>%s</upnp:class>", item.getClazz().getValue()));

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date now = new Date();
        String time = sdf.format(now);
        metadata.append(String.format("<dc:date>%s</dc:date>", time));

        Res res = item.getFirstResource();
        if (res != null) {
            // protocol info
            String protocolinfo = "";
            ProtocolInfo pi = res.getProtocolInfo();
            if (pi != null) {
                protocolinfo = String.format("protocolInfo=\"%s:%s:%s:%s\"", pi.getProtocol(), pi.getNetwork(), pi.getContentFormatMimeType(), pi
                        .getAdditionalInfo());
            }

            // resolution, extra info, not adding yet
            String resolution = "";
            if (res.getResolution() != null && res.getResolution().length() > 0) {
                resolution = String.format("resolution=\"%s\"", res.getResolution());
            }

            // duration
            String duration = "";
            if (res.getDuration() != null && res.getDuration().length() > 0) {
                duration = String.format("duration=\"%s\"", res.getDuration());
            }

            // res begin
            //            metadata.append(String.format("<res %s>", protocolinfo)); // no resolution & duration yet
            metadata.append(String.format("<res %s %s %s>", protocolinfo, resolution, duration));

            // url
            String url = res.getValue();
            metadata.append(url);

            // res end
            metadata.append("</res>");
        }
        metadata.append("</item>");

        metadata.append(DIDL_LITE_FOOTER);

        return metadata.toString();
    }


}
