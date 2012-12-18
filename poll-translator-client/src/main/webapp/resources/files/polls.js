/*global window, document, location, setTimeout, $, d, bro, _gaq, kiss */

/* PARTICIPATION PAGE */

d.initialize = function () {
    var l10n = d.l10n;
    d.part.answerText = {
        "y": l10n.yes,
        "n": l10n.no,
        "i": l10n.ifneedbe,
        "q": "?",
        "h": "#"
    };

    if (d.utils.isIE8Optimized()) {
        d.poll.skip = 0;
    }

    d.utils.subscribeTo("login", d.refetchPoll);
    d.utils.subscribeTo("login", d.refetchUserInfo);
    d.utils.subscribeTo("login", d.part.updateAvatar);
    d.utils.subscribeTo("login", d.updateHelpInformation);
    d.utils.subscribeTo("login", d.part.setSaveStatus);
    d.utils.subscribeTo("login", d.head.updateAnonymousHeader);
    d.utils.subscribeTo("login", d.tabs.updateTabsContainer);

    d.utils.subscribeTo("logout", d.refetchPoll);
    d.utils.subscribeTo("logout", d.refetchUserInfo);
    d.utils.subscribeTo("logout", d.part.updateAvatar);
    d.utils.subscribeTo("logout", d.updateHelpInformation);
    d.utils.subscribeTo("logout", d.part.setSaveStatus);
    d.utils.subscribeTo("logout", d.head.updateAnonymousHeader);
    d.utils.subscribeTo("logout", d.tabs.updateTabsContainer);

    d.utils.subscribeTo("timeZoneChange", function () {
        d.refetchPoll(true);
    });

    d.utils.subscribeTo("pollReload", function () {
        d.part.cancelInlineEdit(true);
        d.part._prefillUserData();
        d.updateHelpInformation();
        d.cmt.updateCommentPart();
        d.updatePollTable();
        d.part.setSaveStatus();
        d.tabs.updateTabsContainer();
        d.head.updateHeader();
    });

    d.utils.subscribeTo("commentsChange", function () {
        var commentMap = d.part.computeLatestCommentMap();
        $.each(d.poll.participants, function (index, participant) {
            if (participant.id > 0) {
                d.part.addParticipantName($("#part" + participant.id), participant, index, commentMap);
            }
        });
    });

    d.utils.subscribeTo("calendarsChange", d.part.updateSyncOptions);

    d.utils.rescueHooks.push(function () {
        return {
            prefs: d.part.participant.preferences.join(""),
            avatar: d.part.participant.avatar,
            noAccordeon: "" + (d.poll.skip <= 0),
            cOptions: d.poll.closeOptions ? d.poll.closeOptions.join(",") : "",
            commentsOpen: "" + $("#cmtAddArrow.expanderUp").size()
        };
    });

    d.part.initCtrlA();

    d.part.initialize();
    d.cmt.initialize();
    d.head.initialize();

    d.part.rescueParticipant();
    d.part._prefillUserData();

    d.head.updateAnonymousHeader();
    d.head.updateHeader();
    d.tabs.updateTabsContainer();
    d.updateHelpInformation();
    d.cmt.updateCommentPart();

    d.utils.subscribeTo("navigateDestruct", d.nav._destruct);
    d.utils.subscribeTo("navigateConstruct", d.nav._construct);

    d.nav.setVetoFn(function (o, n, cb) {
        cb(!d.utils.checkNavHash(n));
    });

    d.utils.subscribeTo("pollReload", function () {
        if (!d.utils.checkNavHash(d.nav.hash())) {
            d.nav.navigateTo("#table");
        }
    });
};

d.refetchPoll = function (optionsOnly, cb) {
    var params = {
        token: d.myDoodle.token,
        locale: d.myDoodle.viewLocale,
        timeZone: d.utils.timeZone,
        adminKey: d.poll.adminKey || "",
        noAccordeon: d.poll.skip <= 0
    };
    if (d.poll.currentInvitee && d.poll.currentInvitee.participantKey) {
        params.participantKey = d.poll.currentInvitee.participantKey;
    }
    $.ajax({
        type: "GET",
        url: "/np/new-polls/" + encodeURIComponent(d.poll.id) + (optionsOnly ? "/options" : "") + "?" + $.param(params),
        success: function (poll) {
            if (!optionsOnly) {
                delete (d.poll.currentInvitee);
            }
            $.extend(d.poll, poll);
            d.utils.trigger("pollReload");
            if (cb) {
                cb();
            }
        }
    });
};

d.refetchUserInfo = function () {
    if (d.myDoodle.user.loggedIn) {
        $("#calLink a").hide();
        $("#sidebar .busy").show();
        setTimeout(function () {
            $.get("/np/users/me", {
                "token": d.myDoodle.token,
                "pollId": d.poll ? d.poll.id : undefined
            }, function (data) {
                $("#sidebar .busy").hide();
                d.myDoodle.user = data;
                d.myDoodle.userComplete = true;
                d.utils.trigger("calendarsChange");
                if (d.myDoodle.user.mandator === "swisscom") {
                    $("#sponsoredLinks").subst(d.utils.generateSwisscom());
                    $("#sponsoredLinks").show();
                }
            }, "json");
        }, 100);
    } else {
        setTimeout(function () {
            $("#sidebar .busy").hide();
            d.myDoodle.userComplete = true;
            d.utils.trigger("calendarsChange");
        }, 100);
    }
};

d.updateHelpInformation = function () {
    $("#pollInformation").addClass("yellowBG");
    var infoCt = $('<div/>');
    if (d.utils.isIE8Optimized()) {
        var infoIE = $('<div class="infoBox fixedContent"/>');
        infoIE.append($('<h5 class="orange spaceEAfter"/>').text(d.l10n.pollIsViewedOptimized));
        infoIE.append($('<p class="spaceDAfter grey"/>').append($('<span/>').text(d.l10n.ie8isSlow)));
        infoIE.append($('<p class="spaceDBefore"/>').append($('<input type="submit" class="submit" />').val(d.l10n.showCompletePoll).click(function () {
            d.poll.notIE8Optimized = true;
            d.updateHelpInformation();
            d.updatePollTable();
        })));
        infoCt.append(infoIE);
    } else if (d.poll.grantWrite && d.poll.askExtra) {
        var info1 = $('<div class="infoBox fixedContent"/>');
        info1.append($('<h5 class="orange spaceEAfter"/>').text(d.l10n.pollRequestsExtraInformation));
        var pollParams = "pollId=" + encodeURIComponent(d.poll.id) + "&adminKey=" + encodeURIComponent(d.poll.adminKey);
        info1.append($('<p/>').append($('<a/>').text(d.l10n.exportPollToExcel).click(function () {
            _gaq.push(['_trackEvent', "papa", "export", "excelAskMore"]);
        }).attr("href", "/export/excel?" + pollParams + "&" + "locale=" + encodeURIComponent(d.myDoodle.viewLocale) + (d.poll.hasTimeZone ? ("&timeZone=" + encodeURIComponent(d.utils.timeZone)) : ""))));
        d._appendShowAll(info1);
        infoCt.append(info1);
    } else if (d.poll.state === "CLOSED") {
        var info7 = $('<div class="infoBox fixedContent"/>').append($('<h5 class="orange spaceEAfter"/>').text(d.l10n.pollIsClosed));
        var finalOption = -1;
        var list = $('<ul id="finalPicks"/>');
        var params = {
            locale: d.myDoodle.viewLocale,
            timeZone: d.utils.timeZone,
            adminKey: d.poll.adminKey || "",
            pollId: d.poll.id || "",
            participantKey: d.poll.participantKey || ""
        };
        $.each(d.poll.finalPicks, function (i, opt) {
            if (opt) {
                finalOption = i;
                var entry = $('<li/>').text(d.poll.optionsText[i]);
                if (d.poll.type === "date") {
                    entry.append($('<span/>').text(" | ").append($('<a/>').attr("href", "/export/ics?optionIndex=" + i + "&" + $.param(params)).click(function () {
                        _gaq.push(['_trackEvent', "papa", "export", "ics"]);
                    }).text(d.l10n.calendarExport)));
                }
                list.append(entry);
            }
        });
        if (finalOption === -1) {
            info7.append($('<p class="spaceDAfter grey"/>').append($('<span/>').text(d.l10n.closedPollMessage)));
        } else {
            info7.append($('<p class="spaceDAfter grey"/>').append($('<span/>').text(d.utils.messageFormat(d.l10n.closedPollFinalOptionIs, d.poll.initiatorName) + " ").append(list)));
        }
        var emailCl = $('<div class="inviteByEmail">').text(d.l10n.informByEmail).click(function () {
            if (d.wp.isWpPl()) {
                d.wp.openWindow(d.wp.composeUrl(d.poll));
            } /*
                 * [Yahoo! Linking] else if (d.poll.createdWithYahoo) { var params = { appId: , param0: , param1: }; window.open("mrd.mail.yahoo.com/gallery?" + $.param(params), "ddl2y"); }
                 */
            else {
                // KISS
                kiss.track("shared", {
                    'outbound medium': 'social',
                    'outbound destination': 'eMail',
                    'outbound name': 'eMail participation link',
                    'sharer source': 'Closed Poll'
                });
                // End of KISS
                setTimeout(function () {
                    window.location = d.poll.socialHookEmail;
                }, 1);
            }
        });
        var fbCl = $('<div class="fbshare">').text(d.l10n.share).click(function () {
            // KISS
            kiss.track("shared", {
                'outbound medium': 'social',
                'outbound destination': 'Facebook',
                'outbound name': 'Share participation link',
                'sharer source': 'Closed Poll'
            });
            // End of KISS
            window.open(d.poll.socialHookFacebook);
        });
        var twCl = $('<div class="tweet">').click(function () {
            // KISS
            kiss.track("shared", {
                'outbound medium': 'social',
                'outbound destination': 'Twitter',
                'outbound name': 'Tweet participation link',
                'sharer source': 'Closed Poll'
            });
            // End of KISS
            window.open(d.poll.socialHookTwitter);
        });
        info7.append($('<div class="socialHookShare spaceCAfter clearfix"/>').appendAll(emailCl, fbCl, twCl));
        if (d.poll.skip > 0) {
            info7.append($('<input type="submit" class="submit" />').val(d.utils.messageFormat(d.l10n.showAllOptions, d.poll.optionsText.length)).click(function () {
                d.part.switchToAllOptions();
            }));
        }
        infoCt.append(info7);
    } else if (d.poll.hasQOptions && d.poll.grantWrite) {
        var infoQuestion = $('<div class="infoBox fixedContent"/>');
        infoQuestion.append($('<h5 class="orange spaceEAfter"/>').text(d.l10n.editedPollAndInvalidatedParticipantsTitle));
        infoQuestion.append($('<p class="grey"/>').append($('<span/>').text(d.l10n.editedPollAndInvalidatedParticipants)));
        d._appendShowAll(infoQuestion);
        infoCt.append(infoQuestion);
    } else if (!d.part._accessParticipantCreate() && d.poll.isByInvitationOnly && !d.poll.grantWrite) {
        var justParticipated = false;
        $.each(d.poll.participants, function (i, val) {
            if (d.utils.store.contains("myParticipations", val.id)) {
                justParticipated = true;
            }
        });
        if (!justParticipated) {
            if (!d.poll.currentInvitee) {
                var info6a = $('<div class="infoBox fixedContent"/>');
                info6a.append($('<h5 class="orange spaceEAfter"/>').text(d.l10n.needInvitationTitle));
                info6a.append($('<p/>').text(d.utils.messageFormat(d.l10n.needInvitationDesc, d.poll.initiatorName)));
                d._appendShowAll(info6a);
                infoCt.append(info6a);
            } else {
                var info6 = $('<div class="infoBox fixedContent"/>');
                info6.append($('<h5 class="orange spaceEAfter"/>').text(d.l10n.youHaveAlreadyParticipated));
                info6.append($('<p/>').text(d.l10n.somebodyElseParticipated));
                d._appendShowAll(info6);
                infoCt.append(info6);
            }
        }
    } else if (!d.poll.grantRead) {
        var info5 = $('<div class="infoBox"/>');
        info5.append($('<h5 class="orange spaceEAfter"/>').text(d.l10n.hiddenPoll));
        info5.append($('<p class="grey"/>').text(d.l10n.hiddenPollExplanation));
        d._appendShowAll(info5);
        infoCt.append(info5);
    } else if (d.poll.example) {
        var info4 = $('<p class="fixedContent" style="background: transparent;"/>');
        var h3 = $('<h3 class="green"/>');
        h3.append($('<p/>').text((d.poll.type === "date" ? d.l10n.exampleDatePollMessage : d.l10n.exampleTextPollMessage) + " "));
        info4.append(h3);
        info4.append($('<a style="font-weight: normal;"/>').attr("href", "/about/services.html").text(d.l10n.learnMoreMain + "…"));
        $("#pollInformation").removeClass("yellowBG");
        infoCt.append(info4);
    } else if (d.poll.columnConstraint > 0) {
        var info8 = $('<div class="infoBox"/>');
        info8.append($('<h5 class="orange spaceEAfter"/>').text(d.l10n.columnConstraintInfoTitle));
        info8.append($('<p class="grey"/>').text(d.l10n.columnConstraintInfo));
        d._appendShowAll(info8);
        infoCt.append(info8);
    } else if (d.poll.participants.length === 0 && d.poll.grantWrite && !d.poll.isByInvitationOnly) {
        var info2 = $('<div class="infoBox"/>');
        info2.append($('<h5 class="orange spaceEAfter"/>').text(d.l10n.nobodyParticipatedYet));
        info2.append($('<p class="spaceDAfter grey"/>').append($('<div/>').text(d.l10n.linkParticipationExplanationLong).append($('<br/>')).append($('<a/>').attr("href", d.poll.prettyUrl).text(d.poll.prettyUrl))));
        var email = $('<div class="inviteByEmail">').text(d.l10n.inviteByEmail).click(function () {
            if (d.wp.isWpPl()) {
                d.wp.openWindow(d.wp.composeUrl(d.poll));
            } /*
                 * [Yahoo! Linking] else if (d.poll.createdWithYahoo) { var params = { appId: , param0: , param1: }; window.open("mrd.mail.yahoo.com/gallery?" + $.params(param), "ddl2y"); }
                 */
            else {
                // KISS
                kiss.track("shared", {
                    'outbound medium': 'social',
                    'outbound destination': 'eMail',
                    'outbound name': 'eMail participation link',
                    'sharer source': 'Empty Poll'
                });
                // End of KISS
                setTimeout(function () {
                    window.location = d.poll.socialHookEmail;
                }, 1);
            }
        });
        var fb = $('<div class="fbshare">').text(d.l10n.share).click(function () {
            // KISS
            kiss.track("shared", {
                'outbound medium': 'social',
                'outbound destination': 'Facebook',
                'outbound name': 'Share participation link',
                'sharer source': 'Empty Poll'
            });
            // End of KISS
            window.open(d.poll.socialHookFacebook);
        });
        var tw = $('<div class="tweet">').click(function () {
            // KISS
            kiss.track("shared", {
                'outbound medium': 'social',
                'outbound destination': 'Twitter',
                'outbound name': 'Tweet participation link',
                'sharer source': 'Empty Poll'
            });
            // End of KISS
            window.open(d.poll.socialHookTwitter);
        });
        info2.append($('<div class="socialHookShare spaceCAfter clearfix"/>').append(email).append(fb).append(tw));
        d._appendShowAll(info2);
        infoCt.append(info2);
    } else if (d.poll.skip > 0) {
        var info3 = $('<div class="infoBox accordeon fixedContent"/>');
        info3.append($('<h5 class="orange spaceEAfter"/>').text(d.l10n.pollIsBigger));
        info3.append($('<p class="spaceDAfter grey"/>').text(d.l10n.expandToParticipate));
        info3.append($('<input type="submit" class="submit" />').val(d.utils.messageFormat(d.l10n.showAllOptions, d.poll.optionsText.length)).click(function () {
            d.part.switchToAllOptions();
        }));
        infoCt.append(info3);
    }

    $("#pollInformation").subst(infoCt);
};

d._appendShowAll = function (target) {
    if (d.poll.skip > 0) {
        target.append($('<p class="spaceDBefore"/>').append($('<input type="submit" class="submit" />').val(d.utils.messageFormat(d.l10n.showAllOptions, d.poll.optionsText.length)).click(function () {
            d.part.switchToAllOptions();
        })));
    }
};

d.updatePollTable = function () {
    var adNodes;
    if (d.poll.type === "date") {
        adNodes = d.calAds.generateAdsForTable();
    }
    $("#ptContainer").subst(d.part._generateTable(d.part._accessParticipantCreate(), d.part.participant, adNodes));
};

d.calAds.generateAdsForTable = function () {
    var ads = [];

    if (d.poll.showAds) {
        var creativesToTrack = [];
        $.each(d.poll.ads, function (index, creative) {
            ads.push(d.calAds.generateAd(creative, creativesToTrack));
        });

        d.calAds.trackImpressions("show", creativesToTrack);
    }

    return ads;
};

d.calAds.generateAd = function (creative, creativesToTrack) {
    if (creative) {
        var adBox = $('<div class="calendarAd"/>');
        var titleFirstLine = $('<div class="adTitle"/>');
        var titleSecondLine = $('<div class="adTitle"/>');
        titleFirstLine.text(creative.shortTitle);
        titleSecondLine.text(creative.shortTitleSecond);

        if (!creative.shortTitle || creative.shortTitle.length === 0) {
            titleSecondLine.addClass("shortTitleBottom");
        }

        if (!creative.shortTitleSecond || creative.shortTitleSecond.length === 0) {
            titleFirstLine.addClass("shortTitleBottom");
        }

        adBox.append(titleFirstLine);
        adBox.append(titleSecondLine);

        d.calAds.appendQtip(adBox, creative, "tableView");

        creativesToTrack.push(creative);

        return adBox;
    } else {
        return null;
    }
};

d.cmt = {};

d.cmt.updateCommentPart = function () {
    var cmtsPart = $('<div id="cmtsPart" class="clearfix"/>');

    d.cmt._addCommentHeaderHtml(cmtsPart);
    var cmtsBody = $('<div id="cmts"/>');
    d.cmt._addCommentBody(cmtsBody);
    cmtsPart.append(cmtsBody);

    if (d.myDoodle.user.loggedIn) {
        d.cmt._loginChanges();
    } else {
        d.cmt._logoutChanges();
    }
    $("#cmtsContainer").subst(cmtsPart);
};

d.cmt._addCommentBody = function (cmts) {
    $.each(d.poll.comments, function (index, comment) {
        cmts.append(d.cmt._generateCommentHtml(comment));
    });
    cmts.children().last().addClass("lastCmt");
    if (d.poll.state === "CLOSED" && (!d.poll.comments || d.poll.comments.length === 0)) {
        cmts.append($('<span>').text(d.l10n.noComments));
    }
};

d.cmt._setFollowBinding = function (follow) {
    follow.unbind('click');
    if (d.myDoodle.user.loggedIn === true) {
        if (!d.myDoodle.user.emailAddress) {
            var node = $('<p style="line-height: 20px;"/>');
            node.append($('<span/>').text(d.l10n.pleaseValidateEmailAddress)).append('<br/>');
            node.append($('<a href="/mydoodle/changeSettings.html?expandedSettings=contact"/>').text(d.l10n.resendValidationEmail + " »"));
            follow.click({
                title: d.l10n.verifyEmail,
                node: node,
                ok: d.l10n.ok,
                okParams: {}
            }, function (e) {
                follow.check(false);
                d.utils.showInformation(e);
            });
        }
    } else if (!(d.poll.grantWrite && d.poll.grantAdminNotification)) {
        follow.check(false);
        follow.click(function () {
            follow.check(false);
            d.myDoodle.lightboxLogin();
        });
    }
};

d.cmt.initialize = function () {
    d.utils.subscribeTo("login", d.cmt._loginChanges);
    d.utils.subscribeTo("logout", d.cmt._logoutChanges);

    d.utils.subscribeTo("pollReload", d.cmt._pollChanged);
};

d.cmt._pollChanged = function () {
    $("#follow").check(d.poll.followEventsStream);
    d.cmt._addCommentBody($("#cmts").empty());
};

d.cmt._generateTitleText = function () {
    var title = "";
    switch (d.poll.comments.length) {
    case 0:
        title = d.l10n.comment;
        break;
    case 1:
        title = d.poll.comments.length + " " + d.l10n.comment;
        break;
    default:
        title = d.poll.comments.length + " " + d.l10n.comments;
    }
    return title;
};

d.cmt._loginChanges = function () {
    d.cmt._setFollowBinding($("#follow"));
    if (d.myDoodle.user.avatarFile) {
        $("#cmtatorAvatar").css("background-image", "url('get/" + encodeURIComponent(d.myDoodle.user.avatarFile) + "')");
    }
    $("#cmtLogin").css("display", "none");
};

d.cmt._logoutChanges = function () {
    d.cmt._setFollowBinding($("#follow"));
    if (!d.myDoodle.user.avatarFile) {
        $("#cmtatorAvatar").css("background-image", "");
    }
    $("#follow").check(false);
    $("#cmtLogin").css("display", "inline");
};

d.cmt._generateDeleteOrLockedIcon = function (comment) {
    var icon = $('<div/>');
    if (!comment.locked) {
        icon.append($('<div class="icon delIcon"/>').attr("title", d.l10n.deleteComment).bind('click', {
            title: d.l10n.confirmCommentDeletion,
            html: comment.commentHTML,
            yes: d.l10n.deleteComment,
            no: d.l10n.cancel,
            yesfn: d.cmt._deleteComment,
            yesParams: {
                commentId: comment.id
            }
        }, d.utils.askConfirmation));
    } else {
        icon.append($('<div class="icon lockedIcon"/>').attr("title", d.l10n.protectedS));
    }
    return icon;
};

d.cmt._toggleArrow = function () {
    $("#cmtAddArrow").toggleClass("expanderUp");
    $("#cmtsAdd").animateToggle();
    return false;
};

d.cmt._addCommentHeaderHtml = function (comments) {
    var header = $('<div class="cmtsHeader" class="clearfix"/>');
    header.append($('<h3 id="cmtTitle" />').text(d.cmt._generateTitleText()));
    if (!d.poll.grantRead) {
        header.append($('<div class="hiddenHint"/>').append($('<h5 class="orange"/>').text(d.l10n.hiddenPollCommentsExplanation)).append($('<p class="grey"/>').text(d.l10n.hiddenPollCommentsExplanationHint)));
    }
    if (d.poll.state === "OPEN") {
        var arrow = $('<a id="cmtAddArrow" class="expander" />').attr("href", "#").text(d.l10n.addComment).click(d.cmt._toggleArrow);

        header.append($('<div class="cmtsLink fixedContent clearfix">').append(arrow));
        var addC = $('<div id="cmtsAdd" class="cmtsAdd" style="display:none;"/>');
        if (d.utils.rescue.commentsOpen === "1" || $("#cmtAddArrow.expanderUp").size() === 1) {
            arrow.addClass("expanderUp");
            addC.show();
        }
        var avatar = $('<div id="cmtatorAvatar" class="avatarSmall"/>');
        if (d.myDoodle.user.avatarFile) {
            avatar.css("background-image", "url('get/" + encodeURIComponent(d.myDoodle.user.avatarFile) + "')");
            avatar.css("background-position", "center center");
        }
        var cForm = $('<form id="cmtForm" class="cmtForm" />');
        cForm.append($('<label class="hiddenAcc" for="newCmttator"/>').attr("title", d.l10n.yourName));
        cForm.append($('<input id="newCmtator" name="commentator" type="text" maxlength="64" class="newCmtator commentData rescueData" />').val(d.part.participant.cmtName ? d.part.participant.cmtName.substr(0, 64) : "").hint(d.l10n.yourName).change(function () {
            d.part.participant.cmtName = $(this).nhVal();
        }));
        cForm.append($('<label class="hiddenAcc" for="newCmt"/>').attr("title", d.l10n.comment));
        var textarea = $('<textarea id="newCmt" name="comment" class="newCmt rescueData" />').val(d.part.participant.cmt);
        cForm.append($('<div/>').append(textarea).append($('<span id="countCmt"/>')));
        textarea.bind('keyup', function () {
            if ($("#newCmt").val().length > 512) {
                $("#newCmt").val($("#newCmt").val().substring(0, 512));
            }
            $("#countCmt").text('(' + (512 - $("#newCmt").val().length) + ')');
            d.part.participant.cmt = textarea.val();
        });
        var checkbox = $('<input id="follow" type="checkbox" name="follow" class="commentData rescueData"/>');
        if (d.poll.grantRead) {
            checkbox.check(d.utils.rescue.hash !== undefined ? d.utils.rescue.follow : d.poll.followEventsStream);
            d.cmt._setFollowBinding(checkbox);
            cForm.append($('<div class="follow" />').append(checkbox).append($('<label for="follow" />').text(d.l10n.subscribeToEvents)));
        }
        cForm.append($('<p id="formError" class="error" style="display:none;"/>'));
        cForm.append($('<div/>').append($('<input id="submitComment" name="submit" type="submit" class="submit impressAds" />').val(d.l10n.saveComment).click(d.cmt._saveComment)));

        addC.append(avatar);
        addC.append(cForm);

        header.append(addC);
    }
    comments.append(header);
};

d.cmt._generateCommentHtml = function (comment) {
    var commentDiv = $('<div class="cmt"/>').attr("id", "cmt" + comment.id);
    $.data(commentDiv, "user", comment.userBehindCommentator);
    var avatar = $('<div class="avatarSmall"/>');
    if (comment.avatar) {
        avatar.css("background", "center center no-repeat url('get/" + encodeURIComponent(comment.avatar) + "')");
    }
    var commentText = $('<div class="cmtCmt">').html(comment.commentHTML); // OK StringToXhtml on Server
    var details = $('<div class="cmtDetails"/>');
    details.append($('<div/>').text(comment.commentedDateTime));
    details.append($('<span/>').text(" | "));
    details.append($('<div/>').text(comment.commentator));
    if (d.poll.state === "OPEN") {

        details.append($('<span/>').text(" | "));
        details.append(d.cmt._generateDeleteOrLockedIcon(comment));
    }
    commentDiv.append($('<div class="cmtAvatar"/>').append(avatar));
    commentDiv.append(commentText);
    commentDiv.append(details);
    return commentDiv;
};

d.cmt._saveComment = function () {
    var errors = $('<span/>');
    var count = 0;
    if ($("#newCmtator").nhVal() === "") {
        errors = $('<span/>').text(d.l10n.pleaseEnterYourName).append($("<br/>"));
        count = count + 1;
    }
    if ($.trim($("#newCmt").val()) === "") {
        errors.append($('<span/>').text(d.l10n.pleaseEnterComment).append($("<br/>")));
        count = count + 1;
    }
    if ($("#newCmt").nhVal().length > 512) {
        errors.append($('<span/>').text(d.utils.messageFormat(d.l10n.maxTextLength, 512)).append($("<br/>")));
        count = count + 1;
    }
    if (count > 0) {
        $("#formError").empty().append(errors);
        $("#formError").animateIn();
        return false;
    } else {
        $("#formError").empty();
        $("#formError").css("display", "none");
    }

    var params = {
        token: d.myDoodle.token,
        locale: d.myDoodle.viewLocale,
        timeZone: d.utils.timeZone,
        adminKey: d.poll.adminKey || "",
        comment: $("#newCmt").val(),
        commentator: $("#newCmtator").nhVal(),
        follow: $("#follow").isChecked()
    };
    if (d.poll.currentInvitee && d.poll.currentInvitee.participantKey) {
        params.participantKey = d.poll.currentInvitee.participantKey;
    }
    var data = $.param(params);
    $("#submitComment").busy();
    $.post("/np/new-polls/" + encodeURIComponent(d.poll.id) + "/comments", data, function (comment, status, xhr) {
        d.poll.comments.push(comment);
        d.poll.followEventsStream = $("#follow").isChecked();

        // KISS
        kiss.track("commented", {
            "Context": "Poll",
            "Poll: ID": d.poll.id,
            "Poll: Example Poll": d.poll.example,
            "Poll: Comments": d.poll.comments ? d.poll.comments.length.length : 0,
            "Poll: Options": d.poll.optionsText.length,
            "Poll: User has write access": d.poll.grantWrite,
            "Poll: Hidden": d.poll.grantRead,
            "Poll: Has TimeZone": d.poll.hasTimeZone,
            "Poll: Level": d.poll.levels,
            "Poll: Type": d.poll.type,
            "Poll: Participants": d.poll.participants ? d.poll.participants.length : 0,
            "Poll: State": d.poll.state.toLowerCase(),
            "Poll: Column Constraint": d.poll.columnConstraint,
            "Poll: Row Constraint": d.poll.rowConstraint,
            "Poll: has location": d.poll.location && typeof d.poll.location.name !== "undefined",
            "Poll: Ask Address": d.poll.askExtra && d.poll.askExtra.address,
            "Poll: Ask eMail": d.poll.askExtra && d.poll.askExtra.email,
            "Poll: Ask Phone": d.poll.askExtra && d.poll.askExtra.phone,
            "Poll: By Invitation": d.poll.isByInvitationOnly,
            "Poll: Category": d.poll.category,
            "content type": "text"
        });
        // End of KISS

        var cHtml = d.cmt._generateCommentHtml(comment);
        $("#cmts").prepend(cHtml);
        $("#cmtsAdd").css("display", "none");
        $("#cmtTitle").text(d.cmt._generateTitleText());
        $("#newCmt").val("");
        $("#newCmtator").val(d.part.participant.cmtName).blur();
        $("#cmtAddArrow").removeClass("expanderUp");
        $("#countCmt").text("");
        d.utils.trigger("commentsChange");
        $("#submitComment").unbusy();
    });

    return false;
};

d.cmt._deleteComment = function (e) {
    var params = {
        token: d.myDoodle.token,
        adminKey: d.poll.adminKey || ""
    };
    if (d.poll.currentInvitee && d.poll.currentInvitee.participantKey) {
        params.participantKey = d.poll.currentInvitee.participantKey;
    }
    $.ajax({
        type: 'POST',
        url: "/np/new-polls/" + encodeURIComponent(d.poll.id) + "/comments/" + encodeURIComponent(e.commentId) + "/delete/?" + $.param(params),
        success: function (data, status, xhr) {
            d.poll.comments = $.map(d.poll.comments, function (cmt) {
                return cmt.id === e.commentId ? null : cmt;
            });
            $("#cmt" + e.commentId).animateOut(function () {
                $("#cmt" + e.commentId).remove();
                $("#cmtTitle").text(d.cmt._generateTitleText());
            });
            d.utils.trigger("commentsChange");
        }
    });
};

d.head = {};

d.head.updateAnonymousHeader = function () {
    var anonymous = $('<div id="anonymousMessage"/>');
    if (!d.myDoodle.user.loggedIn && !d.poll.grantWrite) {
        anonymous.append($('<h5 class="green spaceCBefore" style="padding-bottom:1px;"/>').text(d.l10n.findInCommon));
        anonymous.append($('<p class="info"/>').text(d.l10n.insertNameInInputfieldAndSelect));
    }
    $("#anonymousHeader").subst(anonymous);
};

d.head.updateHeader = function () {
    var avatar = "";
    var infos = $('<div id="pollInfos" class="fixedContent"/>');
    if (d.poll.initiatorAvatar) {
        infos.addClass("withAvatar");
        avatar = $('<div id="avatarBig" class="avatarBig" />').css("background-image", "url('get/" + encodeURIComponent(d.poll.initiatorAvatar) + "')");
    }
    var title = $('<h2 id="pollTitle" style="font-weight: bold;" />').text(d.poll.title);
    var location = "";
    if (d.poll.location !== undefined && d.poll.location.name) {
        var locationTable = $('<table id="extendedLocation"/>');

        var linkUrl = "";
        if (d.poll.location.country && d.poll.location.country === "CH") {
            linkUrl = d.poll.location.telLink ? d.poll.location.telLink : "http://map.search.ch/" + encodeURIComponent(d.poll.location.address || d.poll.location.name);
        } else {
            linkUrl = window.location.protocol + "//maps.google.com/maps?q=" + encodeURIComponent(d.poll.location.address || d.poll.location.name);
        }

        var locationTd = $('<td/>').append($('<a/>').attr("target", "_blank").text(d.poll.location.name).attr("href", linkUrl));
        locationTable.append($('<tr/>').append($('<td class="where"/>').text(d.l10n.where + ": ")).append(locationTd));

        if (d.poll.location.country && d.poll.location.country === "CH") {
            var address = d.poll.location.address || d.poll.location.name;
            var byCar = $('<span/>').append($('<a target="_blank"/>').attr("href", "http://route.search.ch/?to=" + encodeURIComponent(address)).text(d.l10n.getHereByCar)).append($('<span class="blue spacer"/>').text('|'));
            var weatherAndPTLocation = address.replace(/Switzerland/i, '').replace(/Schweiz/i, '').replace(/CH/, ''); // HACK: Weather link does not work if there's a country in the string
            var byPT = $('<span/>').append($('<a target="_blank"/>').attr("href", "http://fahrplan.search.ch/-/" + encodeURIComponent(weatherAndPTLocation)).text(d.l10n.getHereByPublicTransit)).append($('<span class="blue spacer"/>').text('|'));
            var weather = $('<span/>').append($('<a target="_blank"/>').attr("href", "http://meteo.search.ch/" + encodeURIComponent(weatherAndPTLocation)).text(d.l10n.weatherAtLocation)).append($('<span class="blue spacer"/>').text('|'));
            var poweredBy = $('<span class="grey"/>').text(d.l10n.poweredBy);
            var searchIcon = $('<span class="searchIcon"/>');

            var extendedLocationTd = $('<td/>').append(byCar).append(byPT).append(weather).append(poweredBy).append(searchIcon);
            locationTable.append($('<tr class="extended"/>').append($('<td/>')).append(extendedLocationTd));
        }

        location = $('<div id="pollLocation" class="spaceEBefore"/>').append(locationTable);
    }
    var description = $('<div id="pollDescription" class="spaceCBefore" />').html(d.poll.descriptionHTML); // d.poll.description is html from server
    if ($.browser.msie && $.browser.version < 8) {
        description.addClass("isIE7");
    }
    var header = $('<div>').addClass("clearfix fixedContent").append(avatar).append(infos.append(title).append(d.head._generateDetails()).append(location).append(description));
    $("#pollHeader").subst(header);
    if (d.poll.type === "text" && !d.poll.grantWrite) {
        var printicon = $('<a id="textPrint"/>').attr("href", "#").attr("alt", d.l10n.printView).attr("title", d.l10n.printView).click(function () {
            _gaq.push(['_trackEvent', "papa", "export", "print"]);
            var pollParams = "pollId=" + encodeURIComponent(d.poll.id);
            var locParams = "locale=" + encodeURIComponent(d.myDoodle.viewLocale) + (d.poll.hasTimeZone ? ("&timeZone=" + encodeURIComponent(d.utils.timeZone)) : "");
            window.open("/export/pdf?" + pollParams + "&" + locParams);
            return false;
        });
        if ($.browser.msie && $.browser.version < 8) {
            printicon.addClass("isIE7");
        }
        $("#pollHeader").append($('<div class="clearfix"/>').append(printicon));
    }
};

d.head._generateDetails = function () {
    var details = $('<div id="pollDetails" class="grey clearfix spaceEAfter"/>');
    if (d.poll.grantWrite) {
        if (d.poll.state !== "CLOSED") {
            var params = "pollId=" + encodeURIComponent(d.poll.id) + "&adminKey=" + encodeURIComponent(d.poll.adminKey);
            if (d.poll.type === "date") {
                details.append($('<p/>').html(d.utils.messageFormat(d.utils.encode(d.l10n.editYourPoll), '<a href="/polls/wizard.html?' + params + '">', '</a>')));
            } else {
                details.append($('<p/>').html(d.utils.messageFormat(d.utils.encode(d.l10n.editYourPoll), '<a href="/polls/textWizard.html?' + params + '">', '</a>')));
            }
        } else {
            details.append($('<p/>').text(d.l10n.pollClosed2));
        }
    } else {
        var initiatorHTML = d.utils.encode(d.poll.initiatorName);
        if (d.poll.initiatorMeetMePage) {
            initiatorHTML = $('<div/>').append($("<a/>").attr("href", "/" + encodeURIComponent(d.poll.initiatorMeetMePage)).text(d.poll.initiatorName)).html();
        }
        details.append($('<p/>').html(d.utils.messageFormat(d.utils.encode(d.l10n.pollInitiatedBy), initiatorHTML)));
    }
    details.append($('<span/>').text(" | "));

    var nOfParts = d.utils.messageFormat(d.poll.participants.length !== 1 ? d.l10n.participantPl : d.l10n.participantSg, d.poll.participants.length);
    details.append($('<div id="noPart"/>').attr("title", nOfParts).append($('<div class="icon partIcon"/>')).append($('<span id="countParticipants"/>').text(d.poll.participants.length)));
    details.append($('<span/>').text(" | "));
    var nOfCmts = d.poll.comments.length + " " + d.utils.messageFormat(d.poll.comments.length !== 1 ? d.l10n.commentPl : d.l10n.commentSg, d.poll.comments.length);
    details.append($('<div id="noCmt"/>').attr("title", nOfCmts).append($('<div class="icon commentIcon"/>')).append($('<span id="countComments"/>').text(d.poll.comments.length)));
    details.append($('<span/>').text(" | "));
    details.append($('<div id="latestActivity"/>').attr("title", d.l10n.latestActivity).append($('<div class="icon timeIcon"/>')).append($('<span id="lastActivity"/>').text(d.poll.lastActivity)));
    return details;
};

d.head.initialize = function () {
    d.utils.subscribeTo("pollReload", d.head.updateHeader);
    d.utils.subscribeTo("commentsChange", d.head.updateComments);
    d.utils.subscribeTo("participantsChange", d.head.updateParticipants);
};

d.head.updateComments = function () {
    $("#countComments").text(d.poll.comments.length);
    var nOfCmts = d.poll.comments.length;
    var txt = nOfCmts + " " + nOfCmts !== 1 ? d.l10n.commentPl : d.l10n.commentSg;
    $("#noCmt").attr("title", txt);
};

d.head.updateParticipants = function () {
    $("#countParticipants").text(d.poll.participants.length);
    var nOfParts = d.poll.participants.length;
    var txt = d.utils.messageFormat(nOfParts !== 1 ? d.l10n.participantPl : d.l10n.participantSg, nOfParts);
    $("#noPart").attr("title", txt);
};

d.nav._destruct = function (view) {
    if (view === undefined || view === "#table" || view === "" || view === "#" || view.substr(0, 4) === "#cmt") {
        $("#anonymousHeader").hide();
        $("#pollHeader").hide();
        $("#pollInformation").hide();
        $("#beforeTable").hide();
        $("#tabsContainer").hide();
        $("#pollArea").hide();
        $("#cmtsContainer").hide();
        $("#pollHeader").hide();
    } else if (view === "#calendar") {
        $("#anonymousHeader").hide();
        $("#pollHeader").hide();
        $("#beforeTable").hide();
        $("#tabsContainer").hide();
        $("#calendarView").hide();
        $("#cmtsContainer").hide();
        $("#pollHeader").hide();
    } else if (view === "#admin" || view === "#history" || view === "#notifications") {
        $("#anonymousHeader").hide();
        $("#pollHeader").hide();
        $("#pollInformation").hide();
        $("#tabsContainer").hide();
        $("#pollHeader").hide();
        $("#adminView").hide();
    } else if ($.inArray(view, d.utils._inviteContactHashes) >= 0) {
        $("#inviteContact").remove();
    } else if (view === "#close") {
        $("#anonymousHeader").hide();
        $("#pollHeader").hide();
        $("#pollInformation").hide();
        $("#beforeTable").hide();
        $("#tabsContainer").hide();
        $("#pollArea").hide();
        $("#cmtsContainer").hide();
        $("#pollHeader").hide();
        d.utils.fetchAdmin(function () {
            d.admin.close._destroyClosePoll();
        });
    }
};

d.nav._construct = function (view) {
    if (view === "#table" || view === "#" || view === "" || view.substr(0, 4) === "#cmt") {
        $("#tabbusy").busy();
        d.updatePollTable();
        d.updateHelpInformation();
        if (d.part.participant.id) {
            d.part.inlineEdit(d.part.participant);
        }
        $("#anonymousHeader").show();
        $("#pollHeader").show();
        $("#pollInformation").show();
        $("#tabsContainer").show();
        $("#beforeTable").show();
        $("#pollArea").show();
        $("#cmtsContainer").show();
        $("#pollHeader").show();
        $("#tabbusy").unbusy();
    } else if (view === "#calendar") {
        $("#tabbusy").busy();
        $("#anonymousHeader").show();
        $("#pollHeader").show();
        $("#tabsContainer").show();
        $("#calendarView").show();
        $("#cmtsContainer").show();
        $("#pollHeader").show();
        d.dyn.loadAll([d.staticpath.versioned + "/js/jQueryUi/jquery-ui-1.8.20.custom.doodle.min.js", d.staticpath.versioned + "/js/fullCalendar/fullcalendar.min.js", d.staticpath.versioned + "/js/common/calendar.js", d.staticpath.versioned + "/js/polls/cal-part.js"], function () {
            d.calpart.switchToCalendarView($("#calendarView"));
            $("#tabbusy").unbusy();
        });
    } else if (view === "#admin" || view === "#history" || view === "#notifications") {
        $("#tabbusy").busy();
        $("#anonymousHeader").show();
        $("#pollHeader").show();
        $("#tabsContainer").show();
        $("#pollHeader").show();
        $("#adminView").show();
        d.utils.fetchAdmin(function () {
            d.admin.buildAdmin();
            if (view === "#history") {
                d.admin._showHistory();
            } else if (view === "#notifications") {
                d.admin._showNotification();
            }
            $("#tabbusy").unbusy();
        }, view === "#history");
    } else if ($.inArray(view, d.utils._inviteContactHashes) >= 0) {
        d.utils.fetchAdmin(function () {
            d.admin.invCon.buildInviteContact(view !== "#invite");
        });
    } else if (view === "#close") {
        $("#tabbusy").busy();
        $("#anonymousHeader").show();
        $("#pollHeader").show();
        $("#pollInformation").show();
        $("#beforeTable").show();
        $("#tabsContainer").show();
        $("#pollArea").show();
        $("#cmtsContainer").show();
        $("#pollHeader").show();
        d.updateHelpInformation();
        d.utils.fetchAdmin(function () {
            d.admin.close._initClosePoll();
        });
        $("#tabbusy").unbusy();
    }
    d.tabs.setActiveTab();
};

d.part = {};

d.part.answerText = null;

d.part.yin2ord = {
    "y": 2,
    "i": 1,
    "n": 0
};

d.part._generateTable = function (allowParticipation, participant, adNodes) {
    var commentMap = d.part.computeLatestCommentMap();
    var table = $('<table cellspacing="0" cellpadding="0" class="poll"/>').attr("summary", d.poll.title);
    if (d.poll.type === "text") {
        table.addClass("textPoll");
    }
    var classes = ["date month", "date day", "time"];
    var wOptions = null;
    if (d.poll.skip > 0) {
        wOptions = d.poll.weightedOptionsHtmlAccordeon;
    } else {
        wOptions = d.poll.weightedOptionsHtml;
    }

    d.part._addTableHeaders(table, wOptions, classes, false, '<th/>', adNodes);

    d.part._buildAdminHeader();

    if (d.nav.hash() === "#close") {
        d.utils.fetchAdmin(function () {
            d.admin.close.buildCloseHeader(table);
        });
    }

    d.part._addParticipants(table, commentMap);
    if (allowParticipation && d.nav.hash() !== "#close") {
        table.append(d.part._generateParticipationRow(participant));
    }

    if (d.poll.participants.length >= 10) {
        table.addClass("withFooter");
        d.part._addTableHeaders(table, wOptions.slice(0).reverse(), classes.slice(0, wOptions.length).reverse(), true, '<td/>');
    }

    if (d.poll.grantRead && d.poll.participants.length > 0 && d.nav.hash() !== "#close") {
        d.part.addSumsRow(table);
    }

    // make accordeons clickable
    table.find(".asep").click(d.part.switchToAllOptions);

    return table;
};

d.part._prefillUserData = function (participant) {
    if (participant && participant.id) {
        d.part.participant = {
            name: participant.name,
            cmtName: d.part.participant.cmtName || participant.name,
            cmt: participant.cmt,
            preferences: d.part._prefsAsArray(participant.preferences),
            id: participant.id,
            avatar: participant.avatar,
            firstName: participant.firstName,
            lastName: participant.lastName,
            address: participant.address,
            email: participant.email,
            phone: participant.phone
        };
    } else if (d.myDoodle.user.loggedIn && !d.poll.currentInvitee || d.myDoodle.user.loggedIn && d.poll.currentInvitee && d.myDoodle.user.emailAddress === d.poll.currentInvitee.email || d.myDoodle.user.loggedIn && d.poll.currentInvitee && d.poll.currentInvitee.name === "") {
        if (d.part._hasParticipated() === 0) {
            d.part.participant = {
                name: (d.part.participant.name === "" || !d.part.participant.name) ? (d.myDoodle.user.name ? d.myDoodle.user.name.substr(0, 64) : "") : d.part.participant.name,
                cmtName: d.part.participant.cmtName || d.myDoodle.user.name ? d.myDoodle.user.name.substr(0, 64) : "",
                cmt: d.part.participant.cmt,
                firstName: d.myDoodle.user.firstNames,
                avatar: d.myDoodle.user.avatarFile,
                lastName: d.myDoodle.user.lastNames,
                preferences: d.part._prefsAsArray(d.part.participant.preferences),
                address: d.myDoodle.user.postalAddress,
                email: d.myDoodle.user.unverifiedEmailAddress,
                phone: d.myDoodle.user.phoneNumber
            };
        } else {
            d.part.participant = {
                name: d.part.participant.name || "",
                preferences: d.part._prefsAsArray(d.part.participant.preferences),
                cmtName: d.part.participant.cmtName || d.myDoodle.user.name ? d.myDoodle.user.name.substr(0, 64) : "",
                cmt: d.part.participant.cmt
            };
        }
    } else if (d.poll.currentInvitee) {
        if (d.part._hasParticipated() === 0) {
            var names = d.poll.currentInvitee.name.split(" ");

            var fName = "", lName = "";
            $.each(names, function (i, val) {
                if (i === names.length - 1) {
                    lName = val;
                } else {
                    fName += " " + val;
                }
            });
            if (names.length > 0) {
                fName = fName.substring(1);
            }
            var invName = d.poll.currentInvitee.name === "" ? d.poll.currentInvitee.email.substring(0, d.poll.currentInvitee.email.indexOf("@")) : d.poll.currentInvitee.name;
            d.part.participant = {
                name: d.part.participant.name === "" ? invName.substr(0, 64) : d.part.participant.name,
                cmtName: d.part.participant.cmtName || invName.substr(0, 64),
                cmt: d.part.participant.cmt,
                email: d.poll.currentInvitee.email,
                preferences: d.part._prefsAsArray(d.part.participant.preferences),
                firstName: fName,
                lastName: lName
            };
        } else {
            d.part.participant = {
                name: d.part.participant.name || "",
                preferences: d.part._prefsAsArray(d.part.participant.preferences),
                cmtName: d.poll.currentInvitee.name ? d.poll.currentInvitee.name.substr(0, 64) : "",
                cmt: d.part.participant.cmt
            };
        }
    } else {
        d.part.participant = {
            name: d.part.participant.name || "",
            cmtName: d.part.participant.cmtName,
            preferences: d.part._prefsAsArray(d.part.participant.preferences),
            cmt: d.part.participant.cmt
        };
    }
};

d.part._showError = function (txt) {
    var err = $('<p/>').addClass("error part").append($('<span/>').text(txt));
    $("#ptContainer").after(err);
};

d.part._participate = function (callback) {
    var participantId = d.part.participant.id;
    $(".error.part").remove();
    var saveBtns = participantId ? $("#iSave,#extraSave") : $("#save,#extraSave");
    saveBtns.busy();
    var params = {
        token: d.myDoodle.token,
        adminKey: d.poll.adminKey || "",
        name: d.part.participant.name,
        preferences: d.part.participant.preferences.join(""),
        shownCalendars: d.shownCalendars || "",
        optionsHash: d.poll.optionsHash,
        locale: d.myDoodle.viewLocale,
        onCalendarView: d.nav.hash() === "#calendar"
    };
    if (d.poll.currentInvitee && d.poll.currentInvitee.participantKey) {
        params.participantKey = d.poll.currentInvitee.participantKey;
    }
    if (!participantId) {
        var cal = $("#targetCalendar").val();
        params.targetCalendarId = cal !== "-" ? cal : "";
    }
    if (d.poll.askExtra) {
        $.extend(params, {
            firstName: d.part.participant.firstName,
            lastName: d.part.participant.lastName,
            address: d.part.participant.address,
            email: d.part.participant.email,
            phone: d.part.participant.phone
        });
    }
    var data = $.param(params);
    if (participantId) {
        $.ajax({
            type: 'POST',
            url: "/np/new-polls/" + encodeURIComponent(d.poll.id) + "/participants/" + encodeURIComponent(participantId),
            data: data,
            success: function (participant) {
                if (d.poll.columnConstraint) {
                    d.refetchPoll();
                } else {
                    $.each(d.poll.participants, function () {
                        if (this.id === participantId) {
                            this.name = participant.name;
                            this.preferences = participant.preferences;
                            this.firstName = participant.firstName;
                            this.lastName = participant.lastName;
                            this.address = participant.address;
                            this.email = participant.email;
                            this.phone = participant.phone;
                        }
                    });
                }
                d.part.cancelInlineEdit(true);
                d.updatePollTable();
                saveBtns.unbusy();
                if (d.tabs.activeTab !== "tableTab") {
                    d.nav.navigateTo("#table");
                }
                if (callback) {
                    callback();
                }
                d.utils.trigger("participantsChange");
            }
        });
    } else {
        $.ajax({
            type: 'POST',
            url: "/np/new-polls/" + encodeURIComponent(d.poll.id) + "/participants",
            data: data,
            success: function (participant) {
                var trackData = {};
                if (!participant || !participant.id) {
                    d.utils.showErrorHtml(d.utils.encode("The request could not properly be sent to Doodle, probably because it was modified by a Proxy server. Please try again."));
                    // KISS
                    trackData = {
                        "Proxy Error: Poll ID": d.poll.id,
                        "Proxy Error: Server Response ": d.utils.jsonStringify(participant),
                        "Proxy Error: Time Stamp": new Date().getTime()
                    };
                    kiss.track("Proxy Error", trackData);
                    // End of KISS
                    return;
                }

                d.poll.participants.push(participant);
                d.utils.store.append("myParticipations", participant.id);

                // KISS
                trackData = {
                    "Context": "Poll",
                    "Poll: ID": d.poll.id,
                    "Poll: Example Poll": d.poll.example,
                    "Poll: Comments": d.poll.comments ? d.poll.comments.length.length : 0,
                    "Poll: Options": d.poll.optionsText.length,
                    "Poll: User has write access": d.poll.grantWrite,
                    "Poll: Hidden": d.poll.grantRead,
                    "Poll: Has TimeZone": d.poll.hasTimeZone,
                    "Poll: Level": d.poll.levels,
                    "Poll: Type": d.poll.type,
                    "Poll: Participants": d.poll.participants ? d.poll.participants.length : 0,
                    "Poll: State": d.poll.state.toLowerCase(),
                    "Poll: Column Constraint": d.poll.columnConstraint,
                    "Poll: Row Constraint": d.poll.rowConstraint,
                    "Poll: Ask Address": d.poll.askExtra && d.poll.askExtra.address,
                    "Poll: Ask eMail": d.poll.askExtra && d.poll.askExtra.email,
                    "Poll: Ask Phone": d.poll.askExtra && d.poll.askExtra.phone,
                    "Poll: By Invitation": d.poll.isByInvitationOnly,
                    "Poll: has location": d.poll.location && typeof d.poll.location.name !== "undefined",
                    "Poll: Category": d.poll.category,
                    "Participation: On calendar view": params.onCalendarView,
                    "Participation: Used target calendar": typeof params.targetCalendarId !== "undefined"
                };

                if (d.myDoodle.user.loggedIn && d.myDoodle.user.calendars) {
                    $.extend(trackData, {
                        "Poll: Connected Calendars on participation": d.myDoodle.user.calendars.length
                    });
                }

                if (params.onCalendarView) {
                    var publicShownCalCount = 0, privateShownCalCount = 0;

                    d.cal.iterateShownCalendars(function (calendar) {
                        if (calendar.type === "PUBLIC") {
                            publicShownCalCount += 1;
                        } else {
                            privateShownCalCount += 1;
                        }
                    });

                    $.extend(trackData, {
                        "Poll: Public Calendars on participation": $.map(d.publicCalendars || {}, function () {
                            return 1;
                        }).length,
                        "Poll: Shown Public Calendars on participation": publicShownCalCount
                    });

                    if (d.myDoodle.user.loggedIn && d.myDoodle.user.calendars) {
                        $.extend(trackData, {
                            "Poll: Shown Calendars on participation": privateShownCalCount
                        });
                    }
                }

                if (kiss.usedCalAutoSuggest) {
                    $.extend(trackData, {
                        "Participation: Used Calendar Auto Suggest": kiss.usedCalAutoSuggest
                    });
                }

                kiss.track("Participated", trackData);
                // End of KISS

                d.part._goToConfirmation(participant.id);
            }
        });
    }

};

d.part.initialize = function () {
    var save = $('<input type="submit" class="submit" id="save"/>').val(d.l10n.save).click(d.part._save);
    $("#belowTable").append(save);
    d.part.setSaveStatus();
    d.part.updateSyncOptions();
};

d.part._accessParticipantCreate = function () {
    if (d.poll.state === "CLOSED" || d.nav.hash() === "#close") {
        return false;
    }
    if (!d.poll.isByInvitationOnly) {
        return true;
    }
    if (d.part.participant && d.part.participant.id) {
        return true;
    }
    if (d.poll.currentInvitee !== undefined && d.poll.currentInvitee.participantId === undefined) {
        return true;
    }
    return false;
};

d.part._deleteParticipant = function (e, closefn, keepopenfn) {
    if (d.poll.currentInvitee && d.poll.currentInvitee.participantId === e.id) {
        d.poll.currentInvitee.participantId = undefined;
    }
    var params = {
        token: d.myDoodle.token,
        adminKey: d.poll.adminKey || ""
    };
    if (d.poll.currentInvitee && d.poll.currentInvitee.participantKey) {
        params.participantKey = d.poll.currentInvitee.participantKey;
    }
    $.ajax({
        type: 'POST',
        url: "/np/new-polls/" + encodeURIComponent(d.poll.id) + "/participants/" + encodeURIComponent(e.id) + "/delete?" + $.param(params),
        success: function (data) {
            if (d.poll.columnConstraint) {
                d.refetchPoll();
            } else {
                d.poll.participants = $.map(d.poll.participants, function (part) {
                    return part.id === e.id ? null : part;
                });
                d.part.cancelInlineEdit(true);
                d.updatePollTable();
                d.utils.trigger("participantsChange");
                d.updateHelpInformation();
                d.part.setSaveStatus();
            }
            closefn();
        }
    });
};

d.part._goToConfirmation = function (participantId) {
    var params = {
        pollId: d.poll.id,
        adminKey: d.poll.adminKey || "",
        participantId: participantId
    };
    if (d.poll.currentInvitee && d.poll.currentInvitee.participantKey) {
        params.participantKey = d.poll.currentInvitee.participantKey;
    }
    if (d.part.participant.email) {
        params.email = d.part.participant.email;
    }
    setTimeout(function () {
        location.href = "/polls/notifications.html?" + $.param(params);
    }, 100);
};

d.part.addPartCountCell = function (tr, calc, cellTag) {
    var txt = "";
    var klass = "nonHeader";
    var whoismissing = {};
    var nOfParts = d.poll.participants.length;
    if (calc) {
        if (d.poll.isByInvitationOnly) {
            txt = d.utils.messageFormat(d.l10n.nOfmInvitees, d.poll.participants.length, d.poll.nrInvitees);
        } else {
            txt = d.utils.messageFormat(nOfParts !== 1 ? d.l10n.participantPl : d.l10n.participantSg, nOfParts);
        }
        klass += " partCount";
        if (d.poll.grantWrite) {
            whoismissing = $('<p id="whoIsMissing" class="spaceEBefore"/>').append($('<a />').text(d.l10n.whoIsMissingShort).click(function () {
                $("#whoIsMissing a").busy();
                d.utils.fetchAdmin(function () {
                    d.admin.buildWhoIsMissing();
                }, true);
            }));
        }
    }
    if (d.poll.isByInvitationOnly && d.poll.grantWrite) {
        tr.append($(cellTag).addClass(klass).append($('<p/>').text(txt)).append(whoismissing));
    } else {
        tr.append($(cellTag).addClass(klass).text(txt));
    }
};

d.part._addTableHeaders = function (table, wOptions, classes, isFooter, cellTag, adNodes) {
    var nonEmptyAds = $.map(adNodes || [], function (adNode) {
        return adNode;
    });
    var hasAds = !isFooter && adNodes && nonEmptyAds.length > 0 && d.poll.showAds && d.poll.skip === 0;
    $.each(wOptions, function (rowIndex, row) {
        var tr = $('<tr class="header"/>').addClass(classes[rowIndex] + (wOptions.length === 2 ? " notime" : "") + (isFooter ? " foot" : ""));
        d.part.addPartCountCell(tr, (!isFooter && rowIndex + 1 === wOptions.length && d.nav.hash() !== "#close"), cellTag);
        var cumWeight = 0;
        $.each(row, function (colIndex, col) {
            var html = col[0], weight = col[1], sep = col[2];
            cumWeight += weight;
            var p = $('<p/>').html(html);
            var th = $(cellTag).append(p);
            if (d.poll.type === "text" && $.browser.msie && $.browser.version < 9) {
                table.css("table-layout", "fixed");
                th.attr("width", "94px");
                p.css("width", "94px");
            }
            if (weight > 1) {
                th.attr("colspan", weight);
            }
            tr.append(th);
            if (colIndex + 1 === row.length) {
                th.addClass("rsep");
            } else {
                if (colIndex + 1 !== row.length && sep && sep === "a") {
                    tr.append($(cellTag).addClass("asep").append($('<div/>')));
                } else if (sep && sep !== "a") {
                    th.addClass(sep + "sep");
                }
            }
        });
        table.append(tr);

        if (hasAds && rowIndex === 1) {
            var adsTr = $('<tr class="header adsRow"/>');

            adsTr.append($(cellTag));
            $.each(adNodes, function (adIndex, adNode) {
                var adTh = $(cellTag).addClass(adIndex === 0 ? "first" : (adIndex === adNodes.length - 1 ? "last" : ""));
                if (adNode) {
                    adTh.append(adNode);
                }
                var adWeight = row[adIndex][1], adSep = row[adIndex][2];
                if (adWeight > 1) {
                    adTh.attr("colspan", adWeight);
                }
                adsTr.append(adTh);
                if (adIndex + 1 === row.length) {
                    adTh.addClass("rsep");
                } else {
                    if (adIndex + 1 !== row.length && adSep && adSep === "a") {
                        adsTr.append($(cellTag).addClass("asep").append($('<div/>')));
                    } else if (adSep && adSep !== "a") {
                        adTh.addClass(adSep + "sep");
                    }
                }
            });

            table.append(adsTr);
        }
    });
};

d.part._addParticipants = function (table, commentMap) {
    $.each(d.poll.participants, function (row, participant) {
        if (!d.utils.isIE8Optimized() || (d.myDoodle.user.loggedIn && participant.userBehindParticipant === d.myDoodle.user.id || d.poll.currentInvitee && participant.id === d.poll.currentInvitee.participantId)) {
            var tr = $('<tr class="participant"/>');
            if (row === (d.poll.participants.length - 1) && !d.part._accessParticipantCreate() && d.poll.participants.length < 10) {
                tr.addClass("endAccordeon");
            }
            d.part._addParticipantCell(tr, participant, row, commentMap);
            d.part._addPreferenceCells(tr, participant);
            table.append(tr);
        }
    });
};

d.part.addParticipantName = function (partTd, participant, index, latestCommentMap) {
    var userBehind = participant.userBehindParticipant;
    var latestComment = userBehind ? latestCommentMap[userBehind] : null;
    var name = participant.name || d.utils.messageFormat(d.l10n.participantN, index + 1);
    if ($.browser.msie) {
        name = name.substr(0, 20);
    }
    partTd.children(".commentIcon, .pname").remove();
    var nameDiv = $('<div/>').addClass("pname" + (latestComment ? " short" : "")).attr("title", participant.name);
    if (!participant.meetMePage) {
        nameDiv.text(name);
    } else {
        nameDiv.append($('<a/>').attr("href", "/" + participant.meetMePage).text(name));
    }
    partTd.append(nameDiv);
    if (latestComment) {
        var bubble = $('<div/>').addClass("icon commentIcon").click(function () {
            d.nav.navigateTo("#" + "cmt" + latestComment.id);
        });
        partTd.append(bubble);
    }
};

d.part.computeLatestCommentMap = function () {
    var map = {};
    $.each(d.poll.comments, function (index, cmt) {
        var userBehind = cmt.userBehindCommentator;
        if (userBehind && !map[userBehind]) {
            map[userBehind] = cmt;
        }
    });

    d.part.dbg = map;

    return map;
};

d.part.updateAvatar = function () {
    if (!d.part.participant.id) {
        d.part.participant.avatar = d.myDoodle.user.avatarFile;
    }
};

d.part._focusOnSwitch = function () {
    d.part.switchToAllOptions();
    $("#pname").unbind('focus', d.part._focusOnSwitch);
    $("#pname").focus();
};

d.part.inlineDelete = function (e) {
    d.utils.askConfirmationNC({
        data: {
            title: d.utils.messageFormat(d.l10n.confirmParticipantDeletion, e.data.name),
            yes: d.l10n.del,
            no: d.l10n.cancel,
            yesfn: d.part._deleteParticipant,
            yesParams: {
                id: e.data.id
            }
        }
    });
};

d.part.inlineEdit = function (participant) {
    d.part.switchToAllOptions();
    d.part.cancelInlineEdit(false);
    d.part._prefillUserData(participant);
    d.part._updateParticipantViews();

    $("#calendarTab").removeClass("inactive");
    $("#calendarTab").bind('click', function () {
        d.nav.navigateTo("#calendar");
    });
    $("#belowTable").hide();
    $("table.poll .participation").hide().empty();
    var partTr = $("#part" + d.part.participant.id).parent();
    var saveTd = $('<td style="background:#fff;"/>').attr('colspan', d.poll.optionsText.length * 2);
    var saveTr = $('<tr class="inEditFnc"/>').append(saveTd);

    if (d.poll.type === "date") {
        saveTd.append($('<a onclick="return false;" id="iCalendar" class="impressAds"/>').attr("href", "#").text(d.l10n.editInCalendar).click(function () {
            d.nav.navigateTo("#calendar");
        }));
    }

    saveTd.append($('<input type="submit" class="submit" id="iCancel"/>').val(d.l10n.cancel).click(function () {
        d.ads.impressAds();
        d.part.cancelInlineEdit(true);
        return false;
    }));
    saveTd.append($('<input type="submit" class="submit" id="iSave"/>').val(d.l10n.save).click(function () {
        d.ads.impressAds();
        d.part._save();
        return false;
    }));
    saveTd.append($('<input type="hidden" id="participantId" class="rescueData"/>').val(d.part.participant.id));

    partTr.after(saveTr);
    var editTr = d.part._generateParticipationRow(d.part.participant);
    d.part._addInlineAdmin(editTr.children().first(), d.part.participant, true);
    editTr.addClass("inEdit");
    partTr.after(editTr);
    partTr.hide();
};

d.part.cancelInlineEdit = function (participate) {
    if (!d.part.participant.id) {
        return;
    }
    $("table.poll .inEdit").remove();
    $("table.poll .inEditFnc").remove();

    d.part.setSaveStatus();

    $("#part" + d.part.participant.id).parent().show();

    d.part._resetParticipant();
    if (participate) {
        $("table.poll .participation").replaceWith(d.part._generateParticipationRow(d.part.participant));
    }
    d.updatePollTable();
};

d.part._addInlineAdmin = function (td, participant, fixed) {
    var editInline = $('<div class="inlineEdit">');
    var edit = $('<a class="inlineEditIcon"/>').attr('title', d.l10n.editEntry);
    if (fixed) {
        editInline.addClass("active");
    }
    var del = $('<a class="inlineDeleteIcon"/>').attr('title', d.l10n.deleteEntry).click({
        name: participant.name,
        id: participant.id
    }, d.part.inlineDelete);
    editInline.append(edit).append(del);
    if (participant.locked) {
        editInline.empty().append($('<div class="inlineLockedIcon"/>').attr("title", d.l10n.protectedS));
        editInline.addClass("partLocked");
    }
    edit.click(function () {
        d.part.inlineEdit(participant);
    });
    if (!fixed) {
        td.mouseover(function () {
            editInline.show();
        });
        td.mouseout(function () {
            editInline.hide();
        });
    } else {
        td.mouseover(function () {
            editInline.addClass("hover");
        });
        td.mouseout(function () {
            if (!td.parent().hasClass("inEdit")) {
                editInline.removeClass("hover");
            }
        });
    }
    td.prepend(editInline);
};

d.part._addParticipantCell = function (tr, participant, index, commentMap) {
    var partTd = $('<td class="pname"/>');
    var me = (d.myDoodle.user.loggedIn && participant.userBehindParticipant === d.myDoodle.user.id || d.poll.currentInvitee && participant.id === d.poll.currentInvitee.participantId);
    if (participant.id > 0) {
        partTd.attr("id", "part" + participant.id);
    }
    if (me) {
        tr.addClass("partMyself");
    }

    if (!d.part.participant.locked && d.nav.hash() !== "#close" && d.poll.state !== "CLOSED") {
        d.part._addInlineAdmin(partTd, participant, me);
    }

    if (participant.avatar) {
        var avatar = $('<div/>').addClass("avatarSmall");
        avatar.css("background-image", "url('get/" + encodeURIComponent(participant.avatar) + "')");
        avatar.css("background-position", "center");
        partTd.append(avatar);
    } else {
        partTd.addClass("long");
    }
    d.part.addParticipantName(partTd, participant, index, commentMap);

    tr.append(partTd);
};

d.part._addPreferenceCells = function (tr, participant) {

    var gfx = {
        y: "pok", // ollOkGreen.png",
        i: "pi", // ollIfneededbe.png",
        n: "pn", // pollNo.gif",
        q: "pog", // pollOpenGrey.png",
        h: "h" // hiddenPollLock.png"
    };

    var gfxGrey = {
        y: "pokg", // ollOkGrey.png",
        i: "pig", // pollIfneededbeGrey.png",
        n: "pn", // ollNo.gif",
        q: "pog", // ollOpenGrey.png",
        h: "h" // iddenPollLock.png"
    };

    var wOptions = d.poll.weightedOptionsHtml;
    var seps = wOptions[wOptions.length - 1];
    var pp = participant.preferences;
    if (typeof pp === "string") {
        pp = d.part._prefsAsArray(pp);
    }
    var opt;
    for (opt = 0; opt < pp.length; opt++) {
        if (!d.part.isHiddenByAccordeon(opt)) {
            var title = participant.name + ", " + d.poll.optionsText[opt] + ": " + d.part.answerText[pp[opt]];
            var cell = $('<td/>').addClass(pp[opt]).attr("title", title);
            var sep = seps[opt][2];
            var imgArray = gfx;
            if (d.nav.hash() === "#close") {
                if (!d.poll.closeOptions || d.poll.closeOptions[opt] !== "y") {
                    imgArray = gfxGrey;
                    cell.addClass('nonFinal');
                }
            } else if (d.poll.state === "CLOSED" && d.poll.finalPicks && !d.poll.finalPicks[opt]) {
                imgArray = gfxGrey;
                cell.addClass('nonFinal');
            }
            cell.append($('<div/>').addClass("partTableCell").addClass(imgArray[pp[opt]]).append($('<img />').attr("src", d.staticpath.versioned + "/graphics/common/doodlesprite.png").attr("alt", title + ".")));
            if (sep && !d.part.hasAccordeonAfter(opt)) {
                cell.addClass(sep + "sep");
            }
            tr.append(cell);
            if (d.part.hasAccordeonAfter(opt)) {
                var skipText = d.utils.messageFormat(d.l10n.optionsSkipped, d.poll.nbOptionsSkipped);
                tr.append($('<td class="asep"/>').attr("title", skipText));
            }
        }
    }
};
d.part.isHiddenByAccordeon = function (index) {
    if (d.poll.skip === 0) {
        return false;
    }
    var hidden = false;
    $.each(d.poll.skips, function (i, val) {
        if (index > val.start - 1 && index < (val.start + val.length)) {
            hidden = true;
        }
    });
    return hidden;
};
d.part.hasAccordeonAfter = function (index) {
    if (d.poll.skip === 0) {
        return false;
    }
    var accAfter = false;
    $.each(d.poll.skips, function (i, val) {
        if (index === (val.start - 1)) {
            accAfter = true;
            d.poll.nbOptionsSkipped = val.length;
        }
    });
    return accAfter;
};

d.part.answer = function (optId, answer, noRecursive) {
    if (d.poll.rowConstraint && answer === "y" && !noRecursive) {
        var i;
        for (i = 0; i < d.poll.optionsText.length; i++) {
            d.part.answer(i, "n", true);
        }
    }
    if (d.poll.columnConstraint && d.part.sums.CalcSums()[optId] && d.poll.columnConstraint <= d.part.sums.CalcSums()[optId].y && d.part.participant.preferences[optId] !== "y") {
        answer = "n";
    }
    d.part.participant.preferences[optId] = answer;
    $("#box" + optId).removeClass("y i n").addClass(answer);
    if (d.poll.levels === 2) {
        $("#option" + optId).check(answer === "y");
    }
    if (d.calpart) {
        var yin2fc = {
            "y": "yes",
            "i": "ifNeedBe",
            "n": "no"
        };
        d.calpart.answer(optId, yin2fc[answer]);
    }
};

d.part.setName = function (name) {
    d.part.participant.name = name;
    $(".participation #pname").nhVal(name);
    if (d.calpart) {
        d.calpart.setName(name);
    }
};

d.part.switchToAllOptions = function () {
    if (d.poll.skip > 0) {
        d.poll.skip = 0;
        delete (d.poll.weightedOptionsHtmlAccordeon);
        d.updateHelpInformation();
        d.updatePollTable();
        d.part.setSaveStatus();
    }
};

d.part._hasParticipated = function () {
    var myParts = 0;
    if (d.poll.currentInvitee && !d.poll.currentInvitee.id) {
        return 0;
    }
    if (d.myDoodle.user.loggedIn) {
        $.each(d.poll.participants, function () {
            if (this.userBehindParticipant === d.myDoodle.user.id) {
                myParts += 1;
            }
        });
    }
    return myParts;
};

d.part._generateParticipationRow = function (participant) {
    var l10n = d.l10n;
    var options = d.poll.optionsText;

    var boxToggle2 = function (e) {
        d.part.answer($(this).parent().data("id"), $(this).isChecked() ? "y" : "n");
        d.part.switchToAllOptions();
        e.stopPropagation();
    };
    var tdToggle2 = function () {
        d.part.answer($(this).data("id"), $(this).children("input:first").isChecked() ? "n" : "y");
        d.part.switchToAllOptions();
        return false;
    };
    var toggle3 = function () {
        d.part.answer($(this).closest("td").data("id"), $(this).attr("class"));
        d.part.switchToAllOptions();
        return false;
    };

    var tr = $('<tr class="participation"/>').addClass(d.poll.levels === 3 ? " threeLevel" : " twoLevel");

    if (!participant.id && (!d.myDoodle.user.loggedIn || d.part._hasParticipated() === 0)) {
        tr.addClass("partMyself");
    }

    var partTd = $('<div style="width:182px"/>');
    var input = $('<input type="text" name="name" id="pname" class="rescueData" maxlength="64"/>').addClass("inputText");

    if ($.browser.msie && $.browser.version < 8) {
        input.addClass("isIE7");
    }
    input.change(function () {
        d.part.setName($(this).nhVal());
    });
    input.val(participant.id ? participant.name : (d.part.participant.name ? d.part.participant.name.substr(0, 64) : "")).focus(d.part._focusOnSwitch).hint(l10n.yourName);

    if (participant.avatar) {
        var avatar = $('<div class="avatarSmall"/>');
        avatar.css("background-image", "url('get/" + encodeURIComponent(participant.avatar) + "')");
        avatar.css("background-position", "center");
        partTd.append(avatar);
    } else {
        input.addClass("long");
    }
    partTd.append($('<label class="hiddenAcc" for="pname"/>').attr("title", d.l10n.yourName));
    partTd.append(input);
    tr.append($('<td class="pname"/>').append(partTd));

    var wOptions = d.poll.weightedOptionsHtml;
    var seps = wOptions[wOptions.length - 1];
    var sums = d.part.sums.CalcSums();
    $.each(options, function (index, option) {
        if (!d.part.isHiddenByAccordeon(index)) {
            var cell = $('<td/>').attr("id", "box" + index).data("id", index);
            var sep = seps[index][2];
            if (d.part.hasAccordeonAfter(index)) {
                sep = "a";
            }
            if (sep !== "a" && index < options.length - 1) {
                cell.addClass(sep + "sep");
            }
            if (d.poll.levels === 3) {
                var templ = '<a href="#"/>';
                var yes = $(templ).addClass("y").text(l10n.yes).attr("title", option + ": " + l10n.yes).click(toggle3);
                var ifneedbe = $(templ).addClass("i").text("(" + l10n.yes + ")").attr("title", option + ": " + l10n.ifneedbe).click(toggle3);
                var no = $(templ).addClass("n").text(l10n.no).attr("title", option + ": " + l10n.no).click(toggle3);
                var ifneedbeWithHint = ifneedbe;
                if (index === 0) {
                    ifneedbeWithHint = $('<div class="withHint"/>').attr("title", l10n.ifneedbeExplanation);
                    ifneedbeWithHint.appendAll('<div class="icon threeLevelHint"/>', ifneedbe);
                }
                yes.mouseover(function () {
                    $(cell).addClass("ymo");
                }).mouseout(function () {
                    $(cell).removeClass("ymo");
                });
                ifneedbeWithHint.mouseover(function () {
                    $(cell).addClass("imo");
                }).mouseout(function () {
                    $(cell).removeClass("imo");
                });
                no.mouseover(function () {
                    $(cell).addClass("nmo");
                }).mouseout(function () {
                    $(cell).removeClass("nmo");
                });

                cell.appendAll(yes, ifneedbeWithHint, no);
                if (participant.preferences && participant.preferences[index] !== "q") {
                    cell.addClass(participant.preferences[index]);
                }
            } else if (d.poll.rowConstraint) {
                var radio = $('<input type="radio" name="p"/>').attr("id", "option" + index).click(boxToggle2);
                var rlabel = $('<label class="hiddenAcc"/>').attr("for", "option" + index).text(option);
                radio.check(participant.preferences && participant.preferences[index] === "y");
                if (d.poll.columnConstraint && sums[index] && sums[index].y >= d.poll.columnConstraint && participant.preferences[index] !== "y" || (d.poll.optionsAvailable[index] === "n" && participant.preferences[index] !== "y")) {
                    cell.append(rlabel).append(radio.attr("disabled", true)).attr("title", option).attr("disabled", true).addClass("disabled");
                } else {
                    cell.append(rlabel).append(radio).attr("title", option).click(tdToggle2);
                }
            } else {
                var box = $('<input type="checkbox" name="p"/>').attr("id", "option" + index).click(boxToggle2);
                var label = $('<label class="hiddenAcc"/>').attr("for", "option" + index).text(option);
                box.check(participant.preferences && participant.preferences[index] === "y");
                if (d.poll.columnConstraint && sums[index] && sums[index].y >= d.poll.columnConstraint && participant.preferences[index] !== "y" || (d.poll.optionsAvailable[index] === "n" && participant.preferences[index] !== "y")) {
                    cell.append(label).append(box.attr("disabled", true)).attr("title", option).attr("disabled", true).addClass("disabled");
                } else {
                    cell.append(label).append(box).attr("title", option).click(tdToggle2);
                }
            }
            tr.append(cell);
            if (d.part.hasAccordeonAfter(index)) {
                var skipText = d.utils.messageFormat(d.l10n.optionsSkipped, d.poll.nbOptionsSkipped);
                tr.append($('<td class="asep"/>').attr("title", skipText));
            }
        }
    });

    return tr;
};

d.part.sums = {};

d.part.sums.CalcSums = function () {
    var sums = [];
    $.each(d.poll.participants, function (row, participant) {
        var pp = participant.preferences;
        if (typeof pp === "string") {
            pp = d.part._prefsAsArray(pp);
        }
        $.each(pp, function (col, option) {
            sums[col] = sums[col] || {
                y: 0,
                i: 0,
                n: 0
            };
            if (option === 'y' || option === 'i' || option === 'n') {
                sums[col][option] += 1;
            }
        });
    });
    return sums;
};

d.part.sums.getMaxSums = function (sums) {
    var maxSums = {
        y: 0,
        i: 0,
        n: 0
    };
    $.each(sums, function (index, colSums) {
        if (d.part.sums.compareSums(colSums, maxSums) > 0) {
            maxSums = colSums;
        }
    });
    return maxSums;
};

d.part.sums.compareSums = function (a, b) {
    return (a.y + a.i - (b.y + b.i)) || (a.y - b.y);
};

/* get the best options index */
d.part.sums.getBestOptionIndex = function () {
    var sums = d.part.sums.CalcSums();
    var maxSums = d.part.sums.getMaxSums(sums);
    var bestOption;
    var amountOfBestOptions = 0;
    $.each(sums, function (index, colSums) {
        if (maxSums.y === colSums.y && maxSums.i === colSums.i && maxSums.n === colSums.n) {
            if (amountOfBestOptions === 0) {
                bestOption = index;
            }
            amountOfBestOptions++;
        }
    });
    if (amountOfBestOptions > 1) {
        return -1;
    } else {
        return bestOption;
    }
};

d.part.addSumsRow = function (table) {
    var l10n = d.l10n;
    var sums = d.part.sums.CalcSums();

    var tr = $('<tr class="sums"/>');
    var td = $('<td class="nonHeader"/>');
    if (d.poll.levels === 3) {
        td.append($('<span/>').text(l10n.yes)).append('<br/>');
        td.append($('<span/>').text(l10n.ifneedbe)).append('<br/>');
        td.append($('<span/>').text(l10n.no));
    }
    tr.append(td);

    var maxSums = d.part.sums.getMaxSums(sums);
    $.each(sums, function (index, colSums) {
        if (!d.part.isHiddenByAccordeon(index)) {
            var td = $('<td/>');
            if (d.nav.hash() === "#close") {
                if (d.poll.closeOptions && d.poll.closeOptions[index] === "y") {
                    td.addClass('final');
                }
            }
            if (d.part.sums.compareSums(colSums, maxSums) === 0) {
                td.addClass("max");
            }
            if (d.poll.levels === 3) {
                td.append($('<span/>').text(colSums.y)).append('<br/>');
                td.append($('<span/>').text(colSums.i)).append('<br/>');
                td.append($('<span/>').text(colSums.n));
            } else {
                if (d.poll.columnConstraint) {
                    td.html(d.utils.messageFormat(d.l10n.columnCountOfColumnConstraint, d.utils.encode(colSums.y), d.utils.encode(d.poll.columnConstraint)));
                } else {
                    td.text(colSums.y);
                }
            }
            tr.append(td);
            if (d.part.hasAccordeonAfter(index)) {
                tr.append('<td/>');
            }
        }
    });
    table.append(tr);
};

d.part.participant = {};
d.part.participant.preferences = [];

d.part.updateSyncOptions = function () {
    $("#belowTable .targetCalendarContainer,#belowTable .targetCalNag").remove();
    if (d.poll.type === "date" && d.myDoodle.user.calendarsReadyForSync) {
        if (d.myDoodle.user.calendars) {
            d.part.targetCalendar = null;
            if (d.utils.rescue.targetCalendar !== "-") {
                $.each(d.myDoodle.user.calendars, function () {
                    var calendar = this;
                    if (calendar.id === d.utils.rescue.targetCalendar || !d.part.targetCalendar && calendar.primary) {
                        d.part.targetCalendar = calendar.id;
                    }
                });
            } else {
                d.part.targetCalendar = "-";
            }
            delete (d.utils.rescue.targetCalendar);
        }
        var chooser = d.part.generateCalendarChoser("targetCalendar", false);
        $("#save").before($('<span class="targetCalendarContainer"/>').append(chooser));
        d.part.addTargetCalNag($("#targetCalendar"), $("#belowTable"));
        if (!d.myDoodle.user.calendars) {
            $("#targetCalendar").busy();
        }
    }

    d.part.setSaveStatus();
};

d.part.generateCalendarChoser = function (id, rescue) {
    var l10n = d.l10n;

    var label = $('<label/>').attr("for", id).text(l10n.saveToCalendar + " ");
    var dropdown = $('<select name="targetCalendar"/>').addClass(rescue ? "rescueData" : "").attr("id", id);
    dropdown.append($('<option/>').text("- " + l10n.noCalendar + " -").attr("value", "-"));
    var fristPrimaryId;
    if (d.myDoodle.user.calendars) {
        $.each(d.myDoodle.user.calendars, function () {
            var calendar = this;
            if (calendar.writable) {
                var option = $('<option/>').attr("value", calendar.id);
                var name = calendar.name;
                if (!this.primary && !d.myDoodle.user.features.pickSubCalendar) {
                    name = "(*) " + name;
                    option.addClass("nag");
                }
                option.text(name);
                dropdown.append(option);
            }
            if (!fristPrimaryId && calendar.primary) {
                fristPrimaryId = calendar.id;
            }
        });
        dropdown.val(d.myDoodle.user.lastTargetCalendar || fristPrimaryId || "");
        if (d.myDoodle.user.calendars.length >= 1) {
            dropdown.bind("change keyup", function () {
                d.part.setTargetCalendar($(this).val());
            });
        }
    }

    return label.add(dropdown);
};

d.part.addTargetCalNag = function (choser, addTo) {
    $(".targetCalNag", addTo).remove();
    if ($("option:selected", choser).is(".nag")) {
        var nag = $('<div class="targetCalNag spaceEBefore"/>');
        nag.append($('<span/>').text("(*) " + d.l10n.featureRequiresPaidSubscription + " "));
        nag.append($('<a target="_blank" href="/premium/plans.html?linkSource=CalendarChooserPoll"/>').text(d.l10n.learnMoreMain + " …"));
        addTo.append(nag);
    }
};

d.part.initCtrlA = function () {
    $(document).bind("keyup", function (event) {
        if (d.part._isCtrlA(event)) {
            var sums = d.part.sums.CalcSums();
            $.each(d.poll.fcOptions || d.poll.optionsText || [], function (index, val) {
                if (!(d.poll.columnConstraint && sums[index] && sums[index].y >= d.poll.columnConstraint && d.part.participant.preferences[index] !== "y" || (d.poll.optionsAvailable[index] === "n" && d.part.participant.preferences[index] !== "y"))) {
                    d.part.answer(val.id || index, "y");
                }
            });
            return false;
        } else {
            return true;
        }
    }).bind("keydown", function (event) {
        return !d.part._isCtrlA(event);
    });
};

d.part._isCtrlA = function (event) {
    if (event.ctrlKey && event.which === 'A'.charCodeAt(0)) {
        return !$(event.target).is(":text,:password,textarea") && !$("#premiumForm").is(":visible");
    } else {
        return false;
    }
};

d.part._prefsAsArray = function (prefs) {
    if ($.isArray(prefs)) {
        return prefs.slice(0); // clone
    } else if (typeof prefs === "string") {
        return prefs.split("");
    } else {
        return $.map(d.poll.optionsText, function () {
            return "q";
        });
    }
};

d.part._updateParticipantViews = function () {
    $.each(d.part.participant.preferences, function (optId, pref) {
        d.part.answer(optId, pref);
    });
    if (d.poll.askExtra) {
        var p = d.part.participant;
        d.extraF.setExtra(p.firstName, p.lastName, p.address, p.email, p.phone);
    }
    $("#calendarView").toggleClass("editInCal", !!d.part.participant.id);
};

d.part._resetParticipant = function () {
    d.part.participant = {};
    d.part._prefillUserData();
    d.part._updateParticipantViews();
};

d.part.rescueParticipant = function () {
    var id = parseInt(d.utils.rescue.participantId, 10) || undefined;
    d.part.participant = {
        name: d.utils.rescue.pname || "",
        preferences: d.part._prefsAsArray(d.utils.rescue.prefs),
        id: id,
        avatar: id ? d.utils.rescue.avatar : d.myDoodle.user.avatarFile,
        firstName: d.utils.rescue.partFirstName,
        lastName: d.utils.rescue.partLastName,
        address: d.utils.rescue.partAddress,
        email: d.utils.rescue.partEmail,
        phone: d.utils.rescue.partPhone
    };
    d.part._updateParticipantViews();
};

d.part.setTargetCalendar = function (calId) {
    d.part.targetCalendar = calId;
    $("#targetCalendar").val(calId);
    d.part.addTargetCalNag($("#targetCalendar"), $("#belowTable"));
    d.part.setSaveStatus();

    if (d.calpart) {
        d.calpart.setTargetCalendar(calId);
    }
};

d.part._save = function () {
    // check the name field again => old safaris bug
    if (!d.part.participant.name) {
        d.part.participant.name = $("#pname").nhVal() || $("#participantName").nhVal();
    }
    if (!d.part.participant.name) {
        d.part._showError(d.l10n.missingNameErrorMessage);
    } else if (d.poll.askExtra) {
        d.extraF.showDialog(d.part._participate);
    } else {
        d.part._participate();
    }
    return false;
};

d.part.setSaveStatus = function () {
    if (!d.part._accessParticipantCreate()) {
        $("#belowTable").hide();
        $("#belowCalendar").hide();
        if (d.poll.state !== "CLOSED") {
            $("#calendarTab").addClass("inactive");
            $("#calendarTab").unbind('click');
        }
    } else {
        var activeNag = !d.part.participant.id && $("#targetCalendar option:selected").is(".nag");
        var skip = d.poll.skip > 0;
        $("#save").attr("disabled", activeNag || skip);
        $("#belowTable").show();
        $("#calendarTab").removeClass("inactive");
        $("#calendarTab").bind('click', function () {
            d.nav.navigateTo("#calendar");
        });
        $("#belowCalendar").show();
    }
};

d.tabs = {};

d.tabs.activeTab = null;

d.tabs._generateTimeZoneSelector = function () {
    var sel;
    if (d.poll.timeZones) {
        sel = $('<select id="timeZone" name="timeZone" class="rescueData"/>');
        $.each(d.poll.timeZones, function () {
            sel.append($('<option/>').val(this[1]).text(this[0]));
        });
        sel.val(d.utils.timeZone);
        sel.change(function () {
            d.utils.setTimeZone($(this).val());
        });
    }

    return sel;
};

d.tabs.updateTabsContainer = function () {
    $("#tabsContainer").empty().append($('<div style="width:814px;"/>').append(d.tabs._generateTimeZoneSelector).append(d.tabs._buildTabs()));
    d.tabs.setActiveTab();
    $("#tabsContainer .tab");
};

d.tabs._buildTabs = function () {
    if (d.poll.type === "text" && !d.poll.grantWrite) {
        $("#tabsContainer").addClass("invisible");
        return;
    }
    $("#tabsContainer").removeClass("invisible");
    var tabs = $('<div id="tabs" class="clearfix"/>');
    tabs.append($('<div id="tableTab" class="tab"/>').append($('<div class="lTab"/>')).append($('<div class="mTab"/>').text(d.l10n.tableView)).append($('<div class="rTab"/>')).bind('click', function () {
        d.nav.navigateTo("#table");
    }));
    if (d.poll.type === "date") {
        tabs.append($('<div id="calendarTab" class="tab"/>').append($('<div class="lTab"/>')).append($('<div class="mTab"/>').text(d.l10n.calendarView)).append($('<div class="rTab"/>')).bind('click', function () {
            d.nav.navigateTo("#calendar");
        }));
    }
    if (d.poll.grantWrite) {
        tabs.append($('<div id="adminTab" class="tab"/>').append($('<div class="lTab"/>')).append($('<div class="mTab"/>').text(d.l10n.administration)).append($('<div class="rTab"/>')).bind('click', function () {
            d.nav.navigateTo("#admin");
        }));
    }
    var pollParams = "pollId=" + encodeURIComponent(d.poll.id);
    if (d.poll.grantWrite) {
        pollParams += "&adminKey=" + encodeURIComponent(d.poll.adminKey);
    }
    var locParams = "locale=" + encodeURIComponent(d.myDoodle.viewLocale) + (d.poll.hasTimeZone ? ("&timeZone=" + encodeURIComponent(d.utils.timeZone)) : "");
    tabs.append($('<a id="tabPrint"/>').attr("href", "#").attr("alt", d.l10n.printView).attr("title", d.l10n.printView).click(function () {
        var text = $('<div/>').append($('<div/>').text(d.l10n.printToCompare)).append($('<div class="spaceCAfter"/>').text(d.l10n.recommendCalendarConnect)).append($('<div/>').append($('<a/>').attr("href", "/about/calendarIntegrationOverview.html").text(d.l10n.moreAboutCalendarConnect)));
        d.utils.askConfirmation({
            data: {
                title: d.l10n.printView,
                node: text,
                yes: d.l10n.openPrintView,
                no: d.l10n.cancel,
                yesfn: function () {
                    window.open("/export/pdf?" + pollParams + "&" + locParams);
                }
            }
        });
        return false;
    }));
    tabs.append($('<div/>').append($('<div id="tabbusy" style="height:10px">')));

    return tabs;
};

d.tabs.setActiveTab = function () {
    $("#tabPrint").hide();
    var targetTab;
    if (d.nav.hash() === "#admin" || d.nav.hash() === "#history" || d.nav.hash() === "#notifications") {
        targetTab = "adminTab";
    } else if (d.nav.hash() === "#calendar") {
        targetTab = "calendarTab";
    } else {
        targetTab = "tableTab";
        $("#tabPrint").show();
    }

    $(".tab").each(function () {
        $(this).toggleClass("activeTab", $(this).attr("id") === targetTab);
    });
    return false;
};

d.part._buildAdminHeader = function () {
    if (d.poll.grantWrite && d.poll.state === "OPEN") {
        var l10n = d.l10n;
        var bestOptionIndex = d.part.sums.getBestOptionIndex();
        var bestOption = "";
        if (bestOptionIndex === -1) {
            bestOption = l10n.several;
        } else if (bestOptionIndex !== undefined && bestOptionIndex > -1) {
            bestOption = d.poll.optionsText[bestOptionIndex];
        } else {
            bestOption = l10n.undefinedStr;
        }
        var mostPopularL10n = $('<span/>').text(d.poll.type === "date" ? l10n.mostPopularDate + ": " : l10n.mostPopularOption + ": ").addClass('text');
        var mostPopular = $('<span/>').text("" + bestOption).addClass('date');
        var spacer = $('<span/>').text(' | ').addClass('spacer');
        var closePollLink = $('<a />').attr("id", "closeArrow").addClass("expander").text(l10n.closePoll);
        if (d.nav.hash() === "#close") {
            closePollLink.click(d.part._switchToNormalMode);
        } else {
            closePollLink.click(d.part._switchToCloseMode);
        }
        var div = $('<div/>').attr('id', 'inlineCloseHeader').append(mostPopularL10n).append(mostPopular).append(spacer).append(closePollLink);
        $('#beforeTable').subst(div);
    } else {
        $('#beforeTable').empty();
    }
};

d.part._switchToCloseMode = function () {
    d.nav.navigateTo("#close");
    $("#closeArrow").unbind("click");
    $("#closeArrow").click(d.part._switchToNormalMode);
};

d.part._switchToNormalMode = function () {
    d.nav.navigateTo("#");
    $("#closeArrow").unbind("click");
    $("#closeArrow").click(d.part._switchToCloseMode);
};

d.utils._inviteContactHashes = ["#invite", "#contact", "#remind", "#inform"];

d.utils.checkNavHash = function (view) {
    if (d.poll && !d.poll.grantWrite && (view === "#admin" || view === "#history" || view === "#notifications" || view === "#close" || $.inArray(view, d.utils._inviteContactHashes) >= 0)) {
        return false;
    } else if (d.poll.state === "CLOSED" && view === "#close") {
        return false;
    } else if (!d.myDoodle.user.loggedIn && $.inArray(view, d.utils._inviteContactHashes) >= 0) {
        return false;
    } else {
        return true;
    }
};

d.utils.fetchAdmin = function (cb, needEvents) {
    var params = {
        token: d.myDoodle.token,
        locale: d.myDoodle.viewLocale,
        timeZone: d.utils.timeZone,
        adminKey: d.poll.adminKey || ""
    };
    if (d.poll.currentInvitee && d.poll.currentInvitee.participantKey) {
        params.participantKey = d.poll.currentInvitee.participantKey;
    }

    var jobs = [];
    if (needEvents === true) {
        d.poll.eventsTs = d.poll.eventsTs || ("" + new Date().getTime());
        var histParams = $.extend({
            timeStamp: d.poll.eventsTs
        }, params);
        var histUrl = "/np/new-polls/" + encodeURIComponent(d.poll.id) + "/admin/history?" + $.param(histParams);
        jobs.push({
            id: "json|" + histUrl,
            fn: function (id) {
                $.get(histUrl, function (events) {
                    d.poll.events = events;
                    d.dyn.done(id);
                });
            }
        });
    }
    var admJsonUrl = "/np/new-polls/" + encodeURIComponent(d.poll.id) + "/admin?" + $.param(params);
    jobs.push({
        id: "json|" + admJsonUrl,
        fn: function (id) {
            $.get(admJsonUrl, function (admin) {
                d.adm = admin;
                d.dyn.done(id);
            });
        }
    });
    jobs.push(d.dyn.loadJsJob(d.staticpath.versioned + "/js/polls/admin.js"));

    d.dyn.doAllJobs(jobs, function () {
        d.admin.initializeOnce();
        cb();
    });
};

d.utils.resetHistory = function () {
    delete (d.poll.events);
    delete (d.poll.eventsTs);
};

d.utils.generateSwisscom = function () {
    var sponsoredLinks = $('<div id="swisscomLink" class="contentPart"/>');
    if (d.myDoodle.viewLocale === "de") {
        sponsoredLinks.append($('<a target="swisscom"/>').attr("href", "r/SwisscomDE").text("Meeting per Telefonkonferenz durchführen"));
    } else if (d.myDoodle.viewLocale === "it") {
        sponsoredLinks.append($('<a target="swisscom"/>').attr("href", "r/SwisscomIT").text("Svolgere una riunione tramite conferenza telefonica"));
    } else if (d.myDoodle.viewLocale === "fr") {
        sponsoredLinks.append($('<a target="swisscom"/>').attr("href", "r/SwisscomFR").text("Organisez une réunion par conférence téléphonique"));
    } else {
        sponsoredLinks.append($('<a target="swisscom"/>').attr("href", "r/SwisscomEN").text("Conduct meeting by teleconference"));
    }
    return sponsoredLinks;
};

d.utils.isIE8Optimized = function () {
    if (!d.poll.notIE8Optimized && d.poll.state !== "CLOSED") {
        if (d.poll.participants.length * d.poll.optionsText.length > 150) {
            if (!$.browser.msie) {
                return false;
            } else {
                return $.browser.version >= 8 && $.browser.version < 9;
            }
        }
    }
    return false;
};
