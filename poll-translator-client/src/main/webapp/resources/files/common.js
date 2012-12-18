/*global document, window, navigator, location, jQuery, $, Mustache, console, sessionStorage, setTimeout, clearTimeout, googletag, _gaq, _kmq, screen, performance */
"use strict";
var d = {};
var kiss = {};

d.staticpath = {};
var doodleJS = {};
doodleJS.mod = {};

/* General-purpose jQuery plugins */

(function ($) {
    $.fn.hint = function (txt, cb) {
        return this.each(function () {
            $(this).data("hint", txt).focus(function () {
                if ($.trim($(this).val()) === txt) {
                    $(this).val("").removeClass("hintText");
                    if (cb) {
                        cb(false);
                    }
                }
            }).blur(function () {
                if ($.trim($(this).val()) === "" || $.trim($(this).val()) === txt) {
                    $(this).val(txt).addClass("hintText");
                    if (cb) {
                        cb(true);
                    }
                }
            }).trigger("blur");
        });
    };
    $.fn.isHint = function () {
        return $.trim(this.val()) === this.data("hint");
    };
    $.fn.nhVal = function (val) {
        if (arguments.length > 0) {
            this.val(val || this.data("hint"));
            this.toggleClass("hintText", this.isHint());
            return this;
        } else {
            return this.isHint() ? "" : $.trim(this.val());
        }
    };

    $.fn.subst = function (content) {
        return this.empty().append(content);
    };

    $.fn.appendAll = function () {
        var target = this;
        $.each(arguments, function (i, content) {
            target.append(content);
        });
        return target;
    };

    $.fn.check = function (val) {
        return $(this).attr("checked", Boolean(val));
    };

    $.fn.isChecked = function (val) {
        return Boolean($(this).attr("checked"));
    };

    $.fn.addPremiumNag = function () {
        var that = this;
        var premiumNag = $('<div id="premiumNag" class="clearfix"/>').append($('<img/>').attr("src", d.staticpath.versioned + '/graphics/premium/pdLogoLarge.png')).append(
                $('<div/>').append($('<h5 class="blue"/>').text(d.utils.l10n.staticpaidtitle)).append($('<p/>').text(d.utils.l10n.featureRequiresPaidSubscription)).append($('<a href="/premium/plans.html?linkSource=PremiumNag"/>').text(d.utils.l10n.moreOnThat + "...")));
        that.dtip({
            content: premiumNag,
            orientation: "n",
            styleClass: "premiumNag"
        });
    };

    $.fn.busy = function () {
        return this.each(function () {
            $(this).siblings("div.busy").remove();
            $(this).attr("disabled", true).after($('<div class="busy"/>').html('&nbsp;'));
        });
    };
    $.fn.unbusy = function () {
        return this.each(function () {
            $(this).removeAttr("disabled").siblings("div.busy").remove();
        });
    };

    $.fn.animateIn = function (cb) {
        return this.each(function () {
            $(this).slideDown("fast", cb);
        });
    };
    $.fn.animateOut = function (cb) {
        return this.each(function () {
            $(this).slideUp("fast", cb);
        });
    };
    $.fn.animateToggle = function (cb) {
        return this.each(function () {
            if ($(this).css("display") === "none") {
                $(this).slideDown("fast", cb);
            } else {
                $(this).slideUp("fast", cb);
            }
        });
    };
})(jQuery);

/* dTip */
(function ($) {
    var delayTimer;
    var canvasSupported = !!document.createElement("canvas").getContext;

    $.fn.dtip = function (opts) {
        // add the tip elements to the DOM.
        // this requires that div#dTip already exists in the DOM at the time .dtip() is called!
        if (!$("#dTip .mtContent").is("div")) {
            $("#dTip").append('<div class="mtContent"></div><div class="mtAnchor"></div>');
        }

        // declare the containers
        var tt_w = $("#dTip");
        var tt_c = $("#dTip .mtContent");
        var tt_a = $("#dTip .mtAnchor");

        if (opts === "hide") {
            tt_w.hide();
            return;
        }

        // declare the default option values
        var d = {
            content: undefined, // the content of the tooltip
            delay: 250, // how long to wait before showing and hiding the tooltip (ms)
            anchor: "n", // n (top), s (bottom), e (right), w (left)
            offset: 5, // offset in pixels of stem from anchor
            styleClass: "standard", // style class to give to main tooltip element
            show: undefined, // callback called when tooltip is shown
            hide: undefined, // callback called when tooltip is hidden
            anchorWidth: 5, // width of the anchor
            anchorHeight: 12, // height of the anchor
            alwaysShown: false
        };

        // merge the defaults with the user declared options
        var o = $.extend(d, opts);

        // initialize the tooltip
        return this.each(function () {
            // declare them here, so jslint is happy
            var show, hide, hide2, genTriangleCanvas, setTriangleBorders;

            // make sure the anchor element can be referred to below
            var el = $(this);

            // always use the specified content node
            var cont = o.content;

            // declare the variables that check if the mouse is still on the tooltip
            var tHov = false;
            var aHov = true;

            // if the tooltip isn't empty
            if (cont) {
                if (!o.alwaysShown) {
                    // add the hover event
                    el.hover(function () {
                        // show the tooltip
                        aHov = true;
                        show();
                    }, function () {
                        aHov = false;
                        hide();
                    });

                    // add a hover event for the tooltip
                    tt_w.hover(function () {
                        tHov = true;
                    }, function () {
                        tHov = false;
                        setTimeout(function () {
                            if (!aHov) {
                                hide();
                            }
                        }, 20);
                    });
                }
            }

            // show the tooltip
            show = function () {
                // set desired style class
                tt_w.removeClass().addClass(o.styleClass);

                tt_w.css("z-index", "9000");

                // add in the content
                tt_c.children().detach();
                tt_c.append(cont);

                // make sure the tooltip is the right width even if the anchor is flush to the right of the screen
                tt_w.hide().width('').width(tt_w.width());

                // get position of anchor element
                var top = parseInt(el.offset().top, 10);
                var left = parseInt(el.offset().left, 10);

                // get width and height of the anchor element
                var elW = parseInt(el.outerWidth(), 10);
                var elH = parseInt(el.outerHeight(), 10);

                // get width and height of the tooltip
                var tipW = tt_w.outerWidth();
                var tipH = tt_w.outerHeight();

                // calculate the difference between anchor and tooltip
                var w = Math.round((elW - tipW) / 2);

                // calculate position for tooltip
                var mLeft = Math.round(left + w);
                var mTop = Math.round(top + elH + o.offset + o.anchorWidth);

                // position of the arrow
                var aLeft = (Math.round(tipW - o.anchorHeight) / 2) - parseInt(tt_w.css("borderLeftWidth"), 10);
                var aTop = 0;

                // calculate where the anchor should be (east & west)
                if (o.anchor === "e") {
                    aTop = Math.round((tipH / 2) - (o.anchorHeight / 2) - parseInt(tt_w.css("borderRightWidth"), 10));
                    aLeft = -o.anchorWidth - parseInt(tt_w.css("borderRightWidth"), 10);
                    mLeft = left + elW + o.offset + o.anchorWidth;
                    mTop = Math.round((top + elH / 2) - (tipH / 2));
                } else if (o.anchor === "w") {
                    aTop = Math.round((tipH / 2) - (o.anchorHeight / 2) - parseInt(tt_w.css("borderLeftWidth"), 10));
                    aLeft = tipW - parseInt(tt_w.css("borderLeftWidth"), 10);
                    mLeft = left - tipW - o.offset - o.anchorWidth;
                    mTop = Math.round((top + elH / 2) - (tipH / 2));
                }

                // calculate where the anchor should be (north & south)
                if (o.anchor === "n") {
                    aTop = tipH - parseInt(tt_w.css("borderTopWidth"), 10);
                    mTop = top - (tipH + o.offset + (o.anchorHeight / 2));
                } else if (o.anchor === "s") {
                    aTop = -(o.anchorHeight / 2) - parseInt(tt_w.css("borderBottomWidth"), 10);
                    mTop = top + elH + o.offset + (o.anchorHeight / 2);
                }

                // create and position the arrow
                if (canvasSupported) {
                    tt_a.empty().append(genTriangleCanvas(o.anchor, o.anchorWidth, tt_w.css("borderTopColor")));
                } else {
                    setTriangleBorders(tt_a, o.anchor, tt_w.css("borderTopColor"));
                }
                tt_a.css({
                    "margin-left": aLeft + "px",
                    "margin-top": aTop + "px"
                });

                // clear delay timer if exists
                if (delayTimer) {
                    clearTimeout(delayTimer);
                }

                // position the tooltip and show it
                delayTimer = setTimeout(function () {
                    // call the show callback function
                    if (o.show) {
                        o.show.call(this);
                    }

                    tt_w.css({
                        "margin-left": mLeft + "px",
                        "margin-top": mTop + "px"
                    }).show();
                }, o.delay);
            };

            // hide the tooltip
            hide = function () {
                if (!tHov) {
                    // clear delay timer if exists
                    if (delayTimer) {
                        clearTimeout(delayTimer);
                    }

                    // hide the tooltip
                    delayTimer = setTimeout(hide2, o.delay);
                }
            };

            // make a second hide function if the tooltip is set to not auto hide
            hide2 = function () {
                if (!o.alwaysShown) {
                    // if the mouse isn't on the tooltip or the anchor, hide it, otherwise loop back through
                    if (!tHov) {
                        // hide the tooltip
                        tt_w.hide();

                        // call the hide callback function
                        if (o.hide) {
                            o.hide.call(this);
                        }
                    } else {
                        setTimeout(hide, 200);
                    }
                }
            };

            // generate html5 canvas based anchor triangle
            genTriangleCanvas = function (orientation, size, fillStyle) {
                var vertices = {
                    n: [[0, 0], [2, 0], [1, 1]],
                    e: [[1, 0], [1, 2], [0, 1]],
                    s: [[0, 1], [1, 0], [2, 1]],
                    w: [[0, 0], [1, 1], [0, 2]]
                };
                var dimensions = {
                    n: [2, 1],
                    e: [1, 2],
                    s: [2, 1],
                    w: [1, 2]
                };
                var scale = function (xy) {
                    return [xy[0] * size, xy[1] * size];
                };

                var v = vertices[orientation].map(scale);
                var d = scale(dimensions[orientation]);

                var canvas = $('<canvas/>').attr("width", d[0]).attr("height", d[1]);
                var ctx = canvas.get(0).getContext("2d");

                ctx.fillStyle = fillStyle;
                ctx.beginPath();
                ctx.moveTo(v[0][0], v[0][1]);
                ctx.lineTo(v[1][0], v[1][1]);
                ctx.lineTo(v[2][0], v[2][1]);
                ctx.fill();

                return canvas;
            };

            // employ border-hack on anchor triangle
            setTriangleBorders = function (el, orientation, color) {
                var dir = {
                    n: "top",
                    e: "right",
                    s: "bottom",
                    w: "left"
                };
                var opDir = {
                    n: "bottom",
                    e: "left",
                    s: "top",
                    w: "right"
                };

                el.css("border", o.anchorWidth + "px solid transparent");
                el.css("border-" + dir[orientation] + "-color", color);
                el.css("border-" + opDir[orientation], "0");
            };

            if (o.alwaysShown) {
                show();
            }
        });
    };
}(jQuery));

/* Dynamic script loading */

d.dyn = {};

// numbers
d.dyn.status = {}; // loading status per job id
d.dyn.waiting = {}; // array of queued callbacks per job id
d.dyn.done = function (id) {
    // job done: set status, invoke queued callbacks
    d.dyn.status[id] = "done";
    $.each(d.dyn.waiting[id], function () {
        this();
    });
    delete (d.dyn.waiting[id]);
};

d.dyn.doJob = function (id, fn, cb) {
    if (d.dyn.status[id] === "done") {
        // job already done: invoke callback
        cb();
    } else {
        if (d.dyn.status[id] === "pending") {
            // job in progress: queue callback
            d.dyn.waiting[id].push(cb);
        } else {
            // job not started yet: set status, queue callback and start job
            d.dyn.status[id] = "pending";
            d.dyn.waiting[id] = [cb];
            fn(id);
        }
    }
};

d.dyn.doAllJobs = function (jobs, cb) {
    var todo = jobs.length;
    var check = function () {
        todo -= 1;
        if (todo === 0) {
            cb();
        }
    };
    $.each(jobs, function (i, job) {
        d.dyn.doJob(job.id, job.fn, check);
    });
};

d.dyn.loadJsJob = function (url) {
    return {
        id: "js|" + url,
        fn: function (id) {
            $.ajax({
                url: url,
                dataType: "text",
                cache: true,
                success: function (code) {
                    code += '\n d.dyn.done(' + d.utils.jsonQuote(id) + ');';
                    $.globalEval(code);
                }
            });
        }
    };
};

d.dyn.load = function (url, cb) {
    var job = d.dyn.loadJsJob(url);
    d.dyn.doJob(job.id, job.fn, cb);
};

d.dyn.loadAll = function (urls, cb) {
    var jobs = $.map(urls, function (url) {
        return d.dyn.loadJsJob(url);
    });
    d.dyn.doAllJobs(jobs, cb);
};

d.dyn.needUiBloat = function (cb) {
    d.dyn.load(d.staticpath.versioned + "/js/jQueryUi/jquery-ui-1.8.20.custom.doodle.min.js", function () {
        // Init a custom renderer here
        $.ui.autocomplete.prototype._renderItem = function (ul, item) {
            var a = $('<a/>');
            var originalTxt = item.label;
            var txts = d.utils.splitHighlightString(originalTxt.toLowerCase(), item.highlight ? item.highlight.split() : this.term.split());
            var pos = 0;
            $.each(txts, function (i, txt) {
                var realTxt = originalTxt.substr(pos, txt.length);
                a.append($(i % 2 === 0 ? '<span/>' : '<b/>').text(realTxt));
                pos += txt.length;
            });
            return $("<li/>").data("item.autocomplete", item).append(a).appendTo(ul);
        };
        cb();
    });
};

/* Utilities */

d.utils = {};

d.utils.splitHighlightString = function (txt, tokens) {
    var splitRe = new RegExp($.map(tokens, function (token) {
        return d.utils.escapeRegExp(token);
    }).join("|"), "g");

    var parts = [];
    var result = splitRe.exec(txt);
    var pos = 0;
    while (result) {
        parts.push(txt.substring(pos, result.index));
        parts.push(result[0]);
        pos = result.index + result[0].length;
        result = splitRe.exec(txt);
    }
    if (pos < txt.length) {
        parts.push(txt.substring(pos, txt.length));
    }

    return parts;
};

d.utils.escapeRegExp = function (text) {
    var re = /[\-\[\]{}()*+?.,\\\^$|#\s]/g;
    return text.replace(re, "\\$&");
};

d.utils.throttle = function (fn, delay) {
    var timer = null;
    return function () {
        var context = this, args = arguments;
        clearTimeout(timer);
        timer = setTimeout(function () {
            fn.apply(context, args);
        }, delay);
    };
};

d.utils.join2 = function (strArray, separator, lastSeparator) {
    var resultStr = "";
    $.each(strArray, function (i, str) {
        resultStr += str;
        if (i < strArray.length - 2) {
            resultStr += separator;
        } else if (i === strArray.length - 2) {
            resultStr += lastSeparator;
        }
    });
    return resultStr;
};

// converts line breaks to <br/>-tags and HTML-encodes all other text
d.utils.nl2br = function (string) {
    if (string) {
        string = $.map(string.split("\n"), function (val) {
            return d.utils.encode(val);
        }).join('<br/>');
    }
    return string;
};

d.utils.jsonStringify = function (obj) {
    if (window.JSON && typeof JSON.stringify === 'function') {
        d.utils.jsonStringify = JSON.stringify;
        return JSON.stringify(obj);
    } else {
        if (obj === undefined) {
            return undefined;
        } else if (obj === null) {
            return "null";
        } else if ($.isArray(obj)) {
            return "[" + $.map(obj, function (val) {
                // in arrays, undefined must be mapped to null
                // furthermore, do not serialize functions!
                return (val !== undefined && (typeof val !== 'function')) ? d.utils.jsonStringify(val) : "null";
            }).join(",") + "]";
        } else if (typeof obj === 'object') {
            return "{" + $.map(obj, function (val, key) {
                // in objects, undefined values must be dropped.
                // furthermore, do not serialize functions!
                return (val !== undefined && (typeof val !== 'function')) ? d.utils.jsonQuote(key) + ":" + d.utils.jsonStringify(val) : null;
            }).join(",") + "}";
        } else if (typeof obj === 'string') {
            return d.utils.jsonQuote(obj);
        } else {
            return "" + obj;
        }
    }
};

d.utils.jsonQuote = function (str) {
    if (!str || str.length === 0) {
        return "\"\"";
    }

    var b, c = 0;
    var len = str.length;
    var sb = "";
    var t;

    sb += '"';
    var i;
    for (i = 0; i < len; i += 1) {
        b = c;
        c = str.charAt(i);
        switch (c) {
        case '\\':
        case '"':
            sb += '\\';
            sb += c;
            break;
        case '/':
            if (b === '<') {
                sb += '\\';
            }
            sb += c;
            break;
        case '\b':
            sb += "\\b";
            break;
        case '\t':
            sb += "\\t";
            break;
        case '\n':
            sb += "\\n";
            break;
        case '\f':
            sb += "\\f";
            break;
        case '\r':
            sb += "\\r";
            break;
        default:
            if (c < ' ' || (c >= '\u0080' && c < '\u00a0') || (c >= '\u2000' && c < '\u2100')) {
                t = "000" + c.charCodeAt(0).toString(16);
                sb += "\\u" + t.substring(t.length - 4);
            } else {
                sb += c;
            }
        }
    }
    sb += '"';

    return sb;
};

d.utils.log = function (message) {
    if (typeof (console) !== 'undefined' && console !== null) {
        console.log(message);
    }
};

d.utils.getUrlVars = function () {
    var vars = {};

    var params = window.location.search.slice(1).split('&');
    $.each(params, function (i, param) {
        var keyVal = param.split('=');
        if (keyVal[0]) {
            vars[decodeURIComponent(keyVal[0])] = keyVal[1] ? decodeURIComponent(keyVal[1]) : "";
        }
    });

    return vars;
};

d.utils.messageFormat = function (pattern) {
    var re = /'([^']+)'|('')|\{(\d+)\}|([^'{]+)/g;

    var str = "";
    var result = re.exec(pattern);
    while (result) {
        if (result[1]) {
            str += result[1];
        } else if (result[2]) {
            str += "'";
        } else if (result[3]) {
            str += arguments[parseInt(result[3], 10) + 1];
        } else if (result[4]) {
            str += result[4];
        }
        result = re.exec(pattern);
    }

    return str;
};

/*
 * Turn a text with MessageFormat-parameters ({0}, {1}) and an url into a DOM node containing a link. E.g. ("Click {0}here{1} to activate this feature.", "/activate.html") -> DOM-Node("<span>Click <a href="/activate.html">here</a> to activate this feature.</span>")
 */
d.utils.linkify = function (pattern, link) {
    var re = /'([^']+)'|('')|\{(\d+)\}|([^'{]+)/g;
    var part = ["", "", ""];
    var stage = 0;
    var result = re.exec(pattern);
    while (result) {
        if (result[1]) {
            part[stage] += result[1];
        } else if (result[2]) {
            part[stage] += "'";
        } else if (result[3]) {
            if (stage < 2) {
                stage++;
            }
        } else if (result[4]) {
            part[stage] += result[4];
        }
        result = re.exec(pattern);
    }
    var node = $('<span/>');
    node.append(document.createTextNode(part[0]));
    node.append($('<a/>').attr('href', link).text(part[1]));
    node.append(document.createTextNode(part[2]));
    return node;
};

d.utils.isEmailValid = function (email) {
    // now similar to biz.doodle.validator.EMailAddressValidator
    if (email.length > 254 || email.indexOf("@") > 64 || (email.length + 1 - email.indexOf("@")) > 253) {
        return false;
    }
    var pattern = /^[a-zA-Z0-9!#$%&'*+-\/=?\^_`{|}~\-]+$/;
    var local = email.substring(0, email.indexOf("@"));
    if (!pattern.test(local) || local[0] === "." || local[local.length - 1] === "." || local.indexOf("..") > -1) {
        return false;
    }
    if (!d.utils.isDomainValid(email.substring(email.indexOf("@")))) {
        return false;
    }
    return true;
};

d.utils.isPhoneValid = function (phone) {
    // now similar to biz.doodle.validator.PhoneNumberValidator
    phone.replace(/[^+0-9]/g, "");
    var pattern = /^\+?[0-9]{7,20}$/;
    if (!pattern.test(phone)) {
        return false;
    }
    return true;
};

d.utils.isDomainValid = function (domain) {
    var pattern = /^@[^\s]+\.[^\s]+$/;
    return pattern.test(domain);
};

// deprecated, use askConfirmationNC instead!
d.utils.askConfirmation = function (e, cb) {
    d.ads.impressAds();
    d.dyn.needUiBloat(function () {
        var content = $('<p style="word-wrap: break-word;"/>');
        if (e.data.text !== undefined) {
            content.text(e.data.text);
        } else if (e.data.html !== undefined) {
            // deprecated! Use node instead
            // if html is set we expect the html to be encoded correctly
            content.html(e.data.html);
        } else if (e.data.node !== undefined) {
            content.append(e.data.node);
        }
        $("#content").append($('<form/>').submit(function () {
            if (!e.data.yesfn(e.data.yesParams)) {
                $(this).dialog("close");
                $(this).remove();
            }
            return false;
        }).attr("id", "dialog-confirm").append($('<h1 class="orange spaceDAfter"/>').text(e.data.title)).append(content).append($('<input type="submit" class="invisible" style="outline: none; height:0;"/>').val("")));
        var buttons = [{
            text: e.data.yes,
            'class': "yes",
            click: function () {
                if (!e.data.yesfn(e.data.yesParams)) {
                    $(this).dialog("close");
                    $(this).remove();
                }
            }
        }, {
            text: e.data.no,
            'class': "no",
            click: function () {
                $(this).dialog("close");
                $(this).remove();
            }
        }];
        $("#dialog-confirm").dialog({
            resizable: false,
            width: e.data.width || 600,
            zIndex: 8000,
            modal: true,
            buttons: buttons,
            open: function (event, ui) {
                if (cb) {
                    cb();
                }
            }
        });
    });
    return false;
};

d.utils.askConfirmationNC = function (e) {
    d.dyn.needUiBloat(function () {
        var content = $('<p style="word-wrap: break-word;"/>');
        if (e.data.text !== undefined) {
            content.text(e.data.text);
        } else if (e.data.html !== undefined) {
            // deprecated! Use node instead
            // if html is set we expect the html to be encoded correctly
            content.html(e.data.html);
        } else if (e.data.node !== undefined) {
            content.append(e.data.node);
        }
        $("#content").append($('<div/>').attr("id", "dialog-confirm").append($('<h1 class="orange spaceDAfter"/>').text(e.data.title)).append(content));
        var buttons = [{
            text: e.data.yes,
            'class': "yes",
            click: function () {
                var dlg = $(this);
                $("button.yes").busy();
                e.data.yesfn(e.data.yesParams, function () {
                    dlg.dialog("close");
                    dlg.remove();
                }, function () {
                    $("button.yes").unbusy();
                });
            }
        }, {
            text: e.data.no,
            'class': "no",
            click: function () {
                $(this).dialog("close");
                $(this).remove();
            }
        }];
        if (e.data.additionalButtons) {
            var btns = [];
            btns.push(buttons[0]);
            $.each(e.data.additionalButtons, function (i, btn) {
                btns.push({
                    text: btn.text,
                    'class': "btn" + i,
                    click: function () {
                        var dlg = $(this);
                        $("button.btn" + i).busy();
                        btn.fn(btn.params, function () {
                            dlg.dialog("close");
                            dlg.remove();
                        }, function () {
                            $("button.btn" + i).unbusy();
                        });
                    }
                });
            });
            btns.push(buttons[1]);
            buttons = btns;
        }
        $("#dialog-confirm").dialog({
            close: function () {
                if (e.data.closefn) {
                    e.data.closefn();
                }
            },
            resizable: false,
            width: e.data.width || 600,
            zIndex: 8000,
            modal: true,
            buttons: buttons

        });
    });
    return false;
};

d.utils.showInformation = function (e, cb) {
    d.ads.impressAds();
    d.dyn.needUiBloat(function () {
        var content = $('<p style="word-wrap: break-word;"/>');
        if (e.data.text !== undefined) {
            content.text(e.data.text);
        } else if (e.data.html !== undefined) {
            // deprecated! Use node instead
            // if html is set we expect the html to be encoded correctly
            content.html(e.data.html);
        } else if (e.data.node !== undefined) {
            content.append(e.data.node);
        }
        $("#content").append($('<div/>').attr("id", "dialog-info").append($('<h1 class="spaceDAfter"/>').addClass("orange").text(e.data.title)).append(content));
        var buttons = {};
        buttons[e.data.ok] = function () {
            if (e.data.okfn) {
                e.data.okfn(e.data.okParams);
            }
            $(this).dialog("close");
            $(this).remove();
        };
        $("#dialog-info").dialog({
            resizable: false,
            width: e.data.width || 600,
            modal: true,
            buttons: buttons,
            zIndex: 8000,
            open: function (event, ui) {
                if (cb) {
                    cb();
                }
            }
        });
    });
    return false;
};

d.utils.showErrorHtml = function (errorMessageHTML) {
    if ($("#ajaxError").length > 0) {
        $("#ajaxError").prepend($('<p/>').html(errorMessageHTML));
    } else {
        var reloadLink = $('<a href="#"/>').html("<br/>&raquo; " + d.utils.encode(d.utils.l10n.clickToReload)).click(function () {
            location.reload();
            return false;
        });
        $('#content').before($('<div id="ajaxError" class="maintenanceMessage"/>').append($('<p/>').html(errorMessageHTML)).append(reloadLink));
    }
};

d.utils.skipAjaxError = false;
d.utils.initAjaxErrorHandler = function () {
    $('#content').ajaxError(function (e, xhr, settings, exception) {
        var errorJson;
        try {
            errorJson = $.parseJSON(xhr.responseText);
        } catch (e2) {
            // ignore parse errors
        }
        // conflict caused by concurrent modifications etc.
        if (xhr.status === 409 && errorJson) {
            d.utils.askConfirmation({
                data: {
                    title: d.utils.l10n.errorOccurred,
                    node: $('<div/>').append($('<div/>').text(errorJson.messageLocalized)).append($('<div class="spaceDBefore"/>').text(d.utils.l10n.reloadAndRetry)),
                    yes: d.utils.l10n.reloadPage,
                    yesfn: function () {
                        window.location.reload();
                    },
                    no: d.utils.l10n.reloadPageLaterManually
                }
            });
        } else if (xhr.status === 502 && errorJson && errorJson.errorType === "CALENDAR") {
            d.utils.askConfirmation({
                data: {
                    title: d.utils.l10n.errorOccurredCalendarSync,
                    node: $('<div/>').append($('<div/>').text(d.utils.l10n.errorOccurredCalendarSyncExplanation)).append($('<div class="spaceDBefore"/>').text(errorJson.messageLocalized)),
                    yes: d.utils.l10n.reloadPage,
                    yesfn: function () {
                        window.location.reload();
                    },
                    no: d.utils.l10n.reloadPageLaterManually
                }
            });
        } else {
            // not expected js-error
            if (!d.utils.skipAjaxError) {
                if (xhr.status !== 0) {
                    var errorMessageHTML = "&rsaquo; " + d.utils.encode(d.utils.l10n.errorOccurred) + ": " + d.utils.encode(xhr.statusText + " - " + exception) + " (" + xhr.status + ")";
                    d.utils.showErrorHtml(errorMessageHTML);
                    // Report error
                    var ajaxErrorMessage = "AJAX Error " + xhr.status + ", Settings: (" + d.utils.jsonStringify(settings) + ")";
                    d.utils.reportError(ajaxErrorMessage, window.location.href, "unkown");
                    d.utils.trigger("ajaxError");
                }
            }
        }
    });
};
d.utils._errorCount = 0;
d.utils._errorReportingEnabled = false;

d.utils.setupErrorReporting = function () {
    d.utils._oldOnError = window.onerror;
    window.onerror = function (msg, url, line) {
        d.utils.reportError(msg, url, line);
        if (d.utils._oldOnError) {
            return d.utils._oldOnError(msg, url, line);
        } else {
            return false;
        }
    };
    d.utils._errorReportingEnabled = true;
};

d.utils.reportError = function (msg, url, line) {
    if (d.utils._errorReportingEnabled && d.utils._errorCount < 10) {
        d.utils._errorCount++;
        $.ajax({
            type: 'POST',
            url: "/np/js-error/",
            data: {
                location: "" + window.location,
                msg: msg,
                url: url,
                line: line
            }
        });
    }
};

d.utils.noAjaxCacheByDefault = function () {
    $.ajaxSetup({
        cache: false
    });
};

d.utils.rescueHooks = [];

d.utils.getRescueData = function (rescueMap) {
    var rescueFields = [];
    $.each(rescueMap, function (key, val) {
        rescueFields.push(d.utils.jsonQuote(key) + ":" + d.utils.jsonQuote(val));
    });

    return "{" + rescueFields.join(",") + "}";
};

d.utils.getRescueMap = function () {
    var rescueMap = {};
    $(".rescueData").each(function () {
        if (!$(this).is(":checkbox") || $(this).isChecked()) {
            if (!$(this).isHint()) {
                rescueMap[$(this).attr("id")] = $(this).val();
            }
        }
    });

    $.each(d.utils.rescueHooks, function () {
        $.extend(rescueMap, this());
    });

    rescueMap.hash = d.nav.hash();

    if (d.nav.needToConfirmExit()) {
        rescueMap.needToConfirmExit = "true";
        d.nav.needToConfirmExit(false);
    }

    return rescueMap;
};

d.utils.prefillRescuedData = function () {
    if (d.utils.rescue.hash) {
        // do not ask for veto (especially no validation) right after language switch
        d.nav.navigateTo(d.utils.rescue.hash);
    }

    if (d.utils.rescue.needToConfirmExit) {
        d.nav.needToConfirmExit(true);
    }

    $.each(d.utils.rescue, function (key, value) {
        var field = $('[id=' + key.replace(/([^\w])/g, "\\$1") + ']');
        if (field.hasClass("rescueData")) {
            if (field.is(":checkbox")) {
                field.check(value !== "");
            } else {
                field.removeClass("hintText").val(value);
            }
        }
    });
};

d.utils.timeZone = null;

d.utils.setTimeZone = function (timeZone) {
    if (timeZone !== d.utils.timeZone) {
        d.utils.timeZone = timeZone;
        d.utils.trigger("timeZoneChange");
    }
};

d.utils.readCookie = function (name) {
    var nameBeginIndex = document.cookie.indexOf(name + "=");
    if (nameBeginIndex === -1) {
        return null;
    }
    var valueEndIndex = document.cookie.indexOf(";", nameBeginIndex);
    var value = document.cookie.substring(nameBeginIndex + name.length + 1, valueEndIndex !== -1 ? valueEndIndex : document.cookie.length);

    return value;
};

d.utils.deleteCookie = function (name) {
    document.cookie = name + '=; expires=Thu, 01-Jan-70 00:00:01 GMT;';
};

// we migrate slowly to module pattern here
doodleJS.mod.utils = (function ($, d) {
    var handlers = {};
    var eventLog = [];
    return {
        extendWithL10n: function (toExtend, l10n) {
            $.each(l10n, function (key, val) {
                toExtend["l10n_" + key] = val;
            });
        },
        subscribeTo: function (event, handler) {
            handlers[event] = handlers[event] || [];
            if ($.isFunction(handler)) {
                handlers[event].push(handler);
            }
        },
        trigger: function (event, data) {
            eventLog.push([new Date(), event]);
            $.each(handlers[event] || [], function (i, fn) {
                fn(data);
            });
        },
        isUrlValid: function (url) {
            var pattern = /^[^\s]+\.[^\s]+$/;
            return pattern.test(url);
        }
    };
})(jQuery, doodleJS);

d.utils = $.extend(d.utils, doodleJS.mod.utils);

doodleJS.mod.mu = (function ($, d) {
    var mus = {};
    var templates = {};
    var partials = {};

    var doInitTemplates = function (data, callback) {
        $.each(data.templates, function (id, text) {
            templates[id] = text;
            mus[id] = function (data, raw) {
                data = data || {};
                var result = Mustache.to_html(templates[id], data, partials);
                return raw ? result : $(result);
            };
            partials[id] = text;
        });
        $.each(data.partials, function (id, text) {
            partials[id] = text;
        });
        callback();
    };
    var loadTemplates = function (groups, callback) {
        var params = $.param({
            groups: groups
        });
        $.getJSON('/np/templates?' + params, function (data) {
            doInitTemplates(data, callback);
        });
    };
    var initTemplates = function (callback) {
        doInitTemplates(d.templates, callback);
    };
    return {
        loadTemplates: loadTemplates,
        initTemplates: initTemplates,
        tmpl: function (template, model) {
            return mus[template](model);
        }
    };
})(jQuery, doodleJS);

d.mu = $.extend({}, doodleJS.mod.mu);

/* MyDoodle login / logout stub */

d.myDoodle = {};

d.myDoodle.forceLoginNeeded = function (callback, doNotAddEvent, message) {
    if (callback && !doNotAddEvent) {
        d.utils.subscribeTo("logout", function () {
            d.myDoodle.forceLoginNeeded(callback, true, message);
        });
        d.utils.subscribeTo("login", function () {
            d.myDoodle.forceLoginNeeded(callback, true, message);
        });
    }
    if (d.utils.getUrlVars().userId && !d.myDoodle.neededUser) {
        d.myDoodle.neededUser = {};
        d.myDoodle.neededUser.id = d.utils.getUrlVars().userId;
    }
    if (d.myDoodle.user.loggedIn && d.myDoodle.neededUser && d.myDoodle.neededUser.id && d.myDoodle.user.id !== d.myDoodle.neededUser.id) {
        d.nav.forceLoginBusy = true;
        $("#loggedOutContent").remove();
        $("#content").children().not("script").not("#ignoreContentOnLogin").hide();
        if ($("#ignoreContentOnLogin").size() === 0) {
            var loginHeader = $('<div id="ignoreContentOnLogin" class="clearfix spaceBAfter contentPart fixedContent spaceABefore"/>');
            loginHeader.append($('<div class="icon120 mydoodle" style="float:left"/>'));
            loginHeader.append($('<div class="indented content500"/>').append($('<p class="h1 spaceEAfter blue"/>').text(d.myDoodle.l10n.myDoodleAccount)).append($('<p class="h2 spaceBAfter grey"/>').text(d.myDoodle.l10n.logIn)));
            $("#content").append(loginHeader);
        }
        var wrongUser = $($('<div id="loggedOutContent" class="contentPart fixedContent blueBg spaceCBefore spaceCAfter"/>'));
        if (!d.myDoodle.neededUser.name) {
            $.get("/np/users/me/name?userId=" + encodeURIComponent(d.myDoodle.neededUser.id), function (data) {
                $("#wrongUser").text('"' + data + '"');
            });
        }
        wrongUser.append($('<p class="indented h4"/>').html(d.utils.messageFormat(d.myDoodle.l10n.loggedInWrongUser, '<b>' + d.utils.encode(d.myDoodle.user.name) + '</b>', '<b><span id="wrongUser">' + (d.myDoodle.neededUser.name ? d.utils.encode(d.myDoodle.neededUser.name) : "") + '</span></b>', '<a href="#" onclick="d.myDoodle.logout();return false;">', '</a>')));
        $("#content").append(wrongUser);
    } else if (d.myDoodle.user.loggedIn) {
        d.nav.forceLoginBusy = false;
        $("#loggedOutContent").remove();
        $("#content").children().not("script").show();
        $("#ignoreContentOnLogin").remove();
        if (callback) {
            callback();
        }
    } else {
        d.nav.forceLoginBusy = true;
        d.dyn.load(d.staticpath.versioned + "/js/mydoodle/mydoodle.js", function () {
            var loginForm = d.myDoodle.needUserLogin(message);
            $("#loggedOutContent").remove();
            $("#content").children().not("script").not("#ignoreContentOnLogin").hide();
            if ($("#ignoreContentOnLogin").size() === 0) {
                var loginHeader = $('<div id="ignoreContentOnLogin" class="clearfix spaceBAfter contentPart fixedContent spaceABefore"/>');
                loginHeader.append($('<div class="icon120 mydoodle" style="float:left"/>'));
                loginHeader.append($('<div class="indented content500"/>').append($('<p class="h1 spaceEAfter blue"/>').text(d.myDoodle.l10n.myDoodleAccount)).append($('<p class="h2 spaceBAfter grey"/>').text(d.myDoodle.l10n.logIn)));
                $("#content").append(loginHeader);
            }
            $("#content").append(loginForm);
            $("#loggedOutContent").show();
        });
    }
};

d.myDoodle.lightboxLogin = function (loginCb) {
    d.dyn.load(d.staticpath.versioned + "/js/mydoodle/mydoodle.js", function () {
        d.dyn.needUiBloat(function () {
            d.myDoodle.lightboxLoginDialog(loginCb);
        });
    });
    return false;
};

d.myDoodle.needLightboxLogin = function (cb) {
    if (d.myDoodle.user.loggedIn) {
        cb();
    } else {
        d.myDoodle.lightboxLogin(cb);
    }
    return false;
};

d.myDoodle.setBaseUrl = function (url) {
    d.myDoodle.baseUrl = url;
};

d.myDoodle.setFacebookAuthUrl = function (url) {
    d.myDoodle.facebookAuthUrl = url;
};

d.myDoodle.status = function () {
    var l10n = d.utils.l10n;
    var html;
    if (d.myDoodle.user.loggedIn) {
        html = $('<div id="loginLinks"/>');
        html.append($('<a class="mydoodle" href="mydoodle/dashboard.html"/>').text(d.myDoodle.user.unverifiedEmailAddress));
        var actions = $('<span class="actions"/>');

        if (d.myDoodle.user.defaultMandator && d.myDoodle.user.mandatorSwitchTitle) {
            actions.append('<span> | </span>');
            actions.append($('<a href="#"/>').click(function () {
                d.myDoodle._switchMandator();
                return false;
            }).text(d.myDoodle.user.mandatorSwitchTitle));
        }

        actions.append('<span> | </span>');
        actions.append($('<a id="statusDashboard" href="mydoodle/dashboard.html"/>').text(l10n.static_myDoodle));
        actions.append('<span> | </span>');
        actions.append($('<a id="statusManageAccount" href="mydoodle/manageAccount.html"/>').text(l10n.manageAccount));
        actions.append('<span> | </span>');
        actions.append($('<a id="statusLogout" href="#"/>').text(l10n.logOut).click(function () {
            d.myDoodle.logout($("#login"));
            return false;
        }));
        html.append(actions);
    } else {
        html = $('<div id="loginLinks"/>');
        var link = $('<a href="#" id="activateLogin"/>').text(d.myDoodle.l10n.logIn);
        link.click(function () {
            d.dyn.load(d.staticpath.versioned + "/js/mydoodle/mydoodle.js", function () {
                d.myDoodle.lightboxLogin();
            });
            return false;
        });
        html.append(link);
    }
    $("#login").subst(html);
};

/* Switching to Mandator-Subdomain and Back */

// For some Pathnames, we do not want to POST data
d.myDoodle._switchMandatorPostBlacklist = ["/mydoodle/manageAccount.html", "/", "/main.html", "/mydoodle/dashboard.html"];

d.myDoodle._buildSwitchMandatorUrl = function () {
    var query = location.search.replace("?", "");
    var isMandator = d.myDoodle.user.mandator === d.myDoodle.user.defaultMandator ? "true" : "false";
    if (!query) {
        query = "ddbdr=" + isMandator;
    } else if (query.indexOf("ddbdr=") === -1) {
        query += "&ddbdr=" + isMandator;
    } else {
        query = query.replace("ddbdr=true", "ddbdr=" + isMandator).replace("ddbdr=false", "ddbdr=" + isMandator);
    }
    return d.myDoodle.user.mandatorSwitchUrl + location.pathname + "?" + query;
};

d.myDoodle._switchMandator = function () {
    // Get redirect url
    var mandatorUrl = d.myDoodle._buildSwitchMandatorUrl();

    // Gather data to be rescued
    var rescueMap = d.utils.getRescueMap();
    var rescueItemCount = 0;
    $.each(rescueMap, function (index, value) {
        rescueItemCount++;
    });
    if (location.pathname === "/polls/wizard.html") {
        delete (rescueMap.hash); // Make sure we always start at the beginning the wizard (i.e. to enforce validation)
    }

    // Post or Redirect
    if ((rescueItemCount > 1) && ($.inArray(location.pathname, d.myDoodle._switchMandatorPostBlacklist) === -1)) {
        $('#login').append($('<form id="switchMandator" method="post" action="' + mandatorUrl + '"/>'));
        $("#switchMandator").append($('<input type="hidden" name="rescuedData"/>').val(d.utils.getRescueData(rescueMap)));
        $.each(rescueMap, function (name, value) {
            if (name !== "locale" && name !== "rescuedData") {
                $("#switchMandator").append($('<input type="hidden"/>').attr("name", name).val(value));
            }
        });
        $("#switchMandator").submit();
    } else {
        location.href = mandatorUrl + rescueMap.hash;
    }
};

d.myDoodle.logout = function (node) {
    // d.ads.impressAds(); // this kills the logout mechanism
    $.post("/np/mydoodle/ajax/logout", {}, function (data) {
        if (data.success) {
            d.utils.deleteCookie("DoodleIdentification");
            d.utils.deleteCookie("DoodleAuthentication");
            d.myDoodle.user = data.user;
            d.myDoodle.token = data.token;
            d.myDoodle.status(node);
            d.utils.trigger("logout");
        }
    }, "json");
};

/* Footer */

d.footer = {};

d.footer.buildFooter = function () {
    var f = $('<div/>');
    var langText = "";
    $.each(d.footer.lang.languages, function () {
        if (d.myDoodle.viewLocale === this[0]) {
            langText = this[1];
        }
    });
    if (langText === "") {
        $.each(d.footer.lang.languages, function () {
            if (d.myDoodle.viewLocale && d.myDoodle.viewLocale.substring(0, 2) === this[0]) {
                langText = this[1];
            }
        });
    }
    if (langText === "") {
        langText = "en";
    }

    var foot = $('<div class="footerlinks"/>');
    foot.append($('<a id="languageExpander" class="expander"/>').text(langText).click(d.footer.lang.toggleSelector));
    foot.append($('<span/>').text("|"));
    foot.append($('<div/>').append($('<a href="/"/>').text(d.utils.l10n.home)));
    if (!d.myDoodle.mandator || !d.myDoodle.mandator.isMandator) {
        foot.append($('<span/>').text("|"));
        if (d.myDoodle.viewLocale === "de") {
            foot.append($('<div/>').append($('<a href="http://de.blog.doodle.com"/>').text("Blog")));
        } else if (d.myDoodle.viewLocale === "fr") {
            foot.append($('<div/>').append($('<a href="http://fr.blog.doodle.com"/>').text("Blog")));
        } else {
            foot.append($('<div/>').append($('<a href="http://en.blog.doodle.com"/>').text("Blog")));
        }
    }
    foot.append($('<span/>').text("|"));
    foot.append($('<div/>').append($('<a href="about/about.html"/>').text(d.utils.l10n.about)));
    if (!d.myDoodle.mandator || !d.myDoodle.mandator.isMandator) {
        foot.append($('<span/>').text("|"));
        // do not remove the obfuscation - might trigger webwasher or similar not-very-smart applications
        foot.append($('<div/>').append($(['sing.html"/>', 'rti', 'dve', '<a href="/about/a'].reverse().join("")).text(d.utils.l10n.advertising)));
        foot.append($('<span/>').text("|"));
        foot.append($('<div/>').append($('<a href="/about/media.html"/>').text(d.utils.l10n.media)));
    }
    foot.append($('<span/>').text("|"));
    foot.append($('<div/>').append($('<a href="/about/tos.html"/>').text(d.utils.l10n.terms)));
    foot.append($('<span/>').text("|"));
    foot.append($('<div/>').append($('<a href="/about/policy.html"/>').text(d.utils.l10n.privacy)));
    foot.append($('<span/>').text("|"));
    foot.append($('<div/>').append($('<a href="/about/help.html"/>').text(d.utils.l10n.help)));
    foot.append($('<span/>').text("|"));
    foot.append($('<div/>').append($('<a href="/about/contact.html"/>').text(d.utils.l10n.contact)));
    if (!d.myDoodle.mandator || !d.myDoodle.mandator.isMandator) {
        foot.append($('<div/>').text("© Doodle AG").css("white-space", "nowrap"));
    } else {
        foot.append($('<span/>').text("|"));
        foot.append($('<div/>').append($('<a/>').attr("href", d.myDoodle.mandator.premiumLink).text(d.myDoodle.mandator.description)).css("white-space", "nowrap"));
    }
    f.append(foot);
    f.append($('<div id="languageSelector" style="display: none;"/>'));
    return f;
};

d.footer.lang = {};

d.footer.lang.toggleSelector = function () {
    if ($("#languageSelector").is(":empty")) {
        d.footer.lang.createSelector();
    }
    $("#languageExpander").toggleClass("expanderUp");
    $("#languageSelector").animateToggle();
};

d.footer.lang.languages = [["en", "English"], ["en_GB", "English (UK)"], ["de", "Deutsch"], ["fr", "Français"], ["it", "Italiano"], ["ar", "العربية"], ["bg", "български"], ["br", "Brezhoneg"], ["ca", "Català"], ["cs", "Čeština"], ["da", "Dansk"], ["el", "ελληνικά"], ["eo", "Esperanto"], ["es", "Español"], ["eu", "Euskara"], ["fi", "Suomi"], ["hu", "Magyar"],
        ["in", "Bahasa Indonesia"], ["iw", "עִבְרִית"], ["lt", "Lietuvių"], ["nl", "Nederlands"], ["pl", "Polski"], ["pt", "Português"], ["ro", "Română"], ["ru", "Русский"], ["sl", "Slovenščina"], ["sv", "Svenska"], ["tr", "Türkçe"], ["uk", "Українська"], ["zh", "中文简体"], ["zh_TW", "中文繁體"]];

d.footer.lang.droppedLanguages = ["ar", "iw", "eo", "in"];

d.footer.lang.createSelector = function () {
    var langs = $('<form id="languages" method="post" action=""/>');
    $.each(d.footer.lang.languages, function () {
        if ($.inArray(this[0], d.footer.lang.droppedLanguages) < 0) {
            if (d.myDoodle.viewLocale === this[0]) {
                langs.append($('<span/>').text(this[1]));
            } else {
                langs.append($('<a/>').text(this[1]).bind('click', {
                    locale: this[0]
                }, d.footer.lang.changeLanguage)).append(" ");
            }
        }
    });
    $("#languageSelector").append(langs);
};

d.footer.lang.changeLanguage = function (e) {
    var rescueMap = d.utils.getRescueMap();
    $("#languages").append($('<input type="hidden" name="locale"/>').val(e.data.locale));
    $("#languages").append($('<input type="hidden" name="rescuedData"/>').val(d.utils.getRescueData(rescueMap)));
    $.each(rescueMap, function (name, value) {
        if (name !== "locale" && name !== "rescuedData") {
            $("#languages").append($('<input type="hidden"/>').attr("name", name).val(value));
        }
    });
    $("#languages").submit();
};

/* Theming support */
d.utils.theming = {};

/* Remove theme on MyDoodle Logout */
d.utils.theming.removeTheme = function () {
    if (!d.utils.theming.data.keepTheme) {
        $("style[title='premium']").remove();
        $('#logo').unbind('click');
    }
};

/* Add theme on MyDoodle Login */
d.utils.theming.loadTheme = function () {
    $("#page").css("margin-left", "0");
    if (!d.utils.theming.data.keepTheme && (d.myDoodle.user.features.customTheme)) {
        // Remove ads
        $("#skyleft, #skyright, #banner, #background, #rectangle").empty();
        $("#googleAsa").remove();
        if ($.browser.msie && $.browser.version < 8) {
            $("#skyright").append($('<div id="ie7Sky"/>'));
            $("#banner").append($('<div id="ie7Banner"/>'));
        }
        $(".adsStyles").remove();
        d.ads.adtechs = [];
        // Add custom theme
        if (d.meetme) { // MeetMe themes always have the user theme, only adsfree can be added
            $("#skyrightcontainer > #skyright > div").css('width', 'auto');
        } else if (d.myDoodle.user.features.customTheme || d.myDoodle.user.features.useCustomLogo) {
            $.getJSON('np/users/me/theming?token=' + encodeURIComponent(d.myDoodle.token), {}, function (data) {
                if (data !== "") { // Only enable Theme if user has set a theme
                    $("head").append(data.theme);
                    d.utils.theming.initClickHandler(data.deferredHref);
                    $("#skyrightcontainer > #skyright > div").css('width', 'auto');
                }
            }, "html");
        }
    }
};

d.utils.theming.initClickHandler = function (deferredUrl) {
    if (deferredUrl) {
        $('#logo').css("cursor", "pointer").click(function () {
            window.open(deferredUrl);
        });
        $("#banner").css("cursor", "pointer").click(function () {
            window.open(deferredUrl);
        });
        $("#skyrightcontainer").css("cursor", "pointer").click(function () {
            window.open(deferredUrl);
        });
    }
};

/* Init theming support */
d.utils.theming.init = function () {
    // Subscribe to logout
    d.utils.subscribeTo("logout", d.utils.theming.removeTheme);

    // Subscribe to login
    d.utils.subscribeTo("login", d.utils.theming.loadTheme);

    // Set current click url to banners
    if (d.utils.theming.data.deferredThemeClickUrl) {
        d.utils.theming.initClickHandler(d.utils.theming.data.deferredThemeClickUrl);
    }
};

d.nav = (function () {
    var hash;
    var isNeedToConfirmExit = false;
    var vetoFn = false; // new not public anymore, just a setter
    var vetoBusy = false;
    var forceLoginBusy = false;

    var getNormalizedHash = function () {
        var hashStr = window.location.hash;
        return hashStr === "#" ? "" : hashStr;
    };

    var navigateNow = function (veto) {
        vetoBusy = false;
        if (veto) {
            // if we receive a veto, then just revert the change to location.hash
            window.location.hash = hash || "#";
        } else {
            // if there is no veto, then go ahead and trigger destruct, construct etc.
            var oldHash = hash;
            hash = getNormalizedHash();
            if ($("" + hash).size() === 0 && oldHash !== undefined) {
                // FIXME: after closing a poll with the tracking option activate
                // the user sees a blank doodle page, instead of the one with the messages.
                // This seems to fix the symptom, but a better solution should be found.
                if (!(oldHash === "#table" && hash === "#contact")) {
                    d.ads.impressAds();
                }
                d.utils.trigger("navigateDestruct", oldHash);
            }
            d.utils.trigger("navigateConstruct", hash);
        }
    };

    var navigate = function (noVeto) { // should be private, but we have to refactor use navigateTo instead
        if (!vetoBusy && !forceLoginBusy) {
            if (hash !== getNormalizedHash()) {
                if (vetoFn && !noVeto) {
                    vetoBusy = true;
                    vetoFn(hash, getNormalizedHash(), navigateNow);
                } else {
                    navigateNow(false);
                }
            }
        }
    };

    return {
        // this is the hash where the app is currently TODO should be only a getter
        hash: function (set) {
            if (set) {
                hash = set;
            }
            return hash;
        },

        setVetoFn: function (set) {
            if (set) {
                vetoFn = set;
            }
        },

        // does a alert come if user exits page?
        needToConfirmExit: function (set) {
            if (set !== undefined) {
                isNeedToConfirmExit = set;
            }
            return isNeedToConfirmExit;
        },

        // this is the hash that is in the url (the one where the app probably isn't yet
        getNormalizedHash: getNormalizedHash,

        // navigate to given hash, if no hash: just navigate
        navigateTo: function (lHash) {
            if (lHash === "" || lHash) {
                // if no parameter then do not set just do the navigation
                window.location.hash = lHash;
            }
            navigate();
            return false;
        },

        // set that the user gets notified if he is leaving the page without continuing
        initConfirmExit: function (str) {
            window.onbeforeunload = function (arg) {
                if (isNeedToConfirmExit) {
                    var e = arg || window.event;
                    // For IE and Firefox prior to version 4
                    if (e) {
                        e.returnValue = str;
                    }
                    return str;
                }
            };
        }
    };
}());

d.utils.encode = function (text) {
    return $('<div/>').text(text).html();
};

d.utils.decode = function (html) {
    return $('<div/>').html(html).text();
};

d.utils.store = {};

/*
 * d.utils.store.store = function(varName, value) { sessionStorage[varName] = value; };
 */
d.utils.store.readList = function (varName) {
    if (window.sessionStorage) {
        if (sessionStorage[varName]) {
            return sessionStorage[varName].split("|");
        } else {
            return [];
        }
    }
};

d.utils.store.append = function (varName, value) {
    if ($.browser.msie && $.browser.version < 8 || !window.sessionStorage) {
        return;
    }
    if (window.sessionStorage[varName] !== undefined) {
        sessionStorage[varName] = sessionStorage[varName] + "|" + value;
    } else {
        sessionStorage[varName] = value;
    }
};

d.utils.store.contains = function (varName, value) {
    if ($.browser.msie && $.browser.version < 8 || !window.sessionStorage) {
        return false;
    }
    var contains = false;
    $.each(d.utils.store.readList(varName), function (i, val) {
        if (val === value + '') {
            contains = true;
        }
    });
    return contains;
};

/* User utilities methods */
d.utils.user = {};

d.utils.user.hasPermission = function (permission) {
    return d.myDoodle.user.loggedIn && $.inArray(permission, d.myDoodle.user.permissions) !== -1;
};

/* Doodle UI Elements */

d.ui = {};

d.ui._btnDown = function () {
    $(this).addClass("pressed");
};

d.ui._btnUp = function () {
    $(this).removeClass("pressed");
};

d.ui.generateBigButton = function (value, linkHref) {
    var href = linkHref || "#";
    var bigButton = $('<a/>').attr("href", href).addClass('bigButton');
    bigButton = bigButton.append($('<div class="left"/>')).append($('<div class="center"/>').text(value)).append($('<div class="right"/>'));
    bigButton.mousedown(d.ui._btnDown).bind("mouseup mouseout", d.ui._btnUp);

    return bigButton;
};

d.ui.generateMediumButton = function (value, linkHref) {
    var href = linkHref || "#";
    var bigButton = $('<a/>').attr("href", href).addClass('mediumButton');
    bigButton = bigButton.append($('<div class="left"/>')).append($('<div class="center"/>').text(value)).append($('<div class="right"/>'));
    bigButton.mousedown(d.ui._btnDown).bind("mouseup mouseout", d.ui._btnUp);

    return bigButton;
};

d.ui.replaceJsfCommandLink = function () {
    $(".jsfBigButton").each(function (index, element) {
        $(element).hide();
        $(element).after($(d.ui.generateBigButton($(element).val()).click(function () {
            $(element).click();
            return false;
        })));
    });
};

(function ($) {
    $.colorPicker = function (element, options) {
        var defaults = {};
        var plugin = this;
        plugin.settings = {};
        plugin.init = function () {
            plugin.settings = $.extend({}, defaults, options);
            d.dyn.load(d.staticpath.versioned + "/js/common/colorpicker.js", function () {
                $(element).find('input').colorpicker({
                    'format': 'hex'
                }).on('changeColor', function (ev) {
                    $(element).find('span').css('background-color', ev.color.toHex());
                    $(element).find('input').val(ev.color.toHex());
                });

                $(element).find('span, input').click(function () {
                    $(element).find('input').focus();
                });

                $(element).find('input').focus(function () {
                    $(element).find('span').addClass('focus');
                });

                $(element).find('input').blur(function () {
                    $(element).find('span').css('background-color', $(element).find('input').val());
                    $(element).find('span').removeClass('focus');
                });
            });
        };
        plugin.init();
    };
    $.fn.colorPicker = function (options) {
        return this.each(function () {
            if (undefined === $(this).data('colorPicker')) {
                var plugin = new $.colorPicker(this, options);
                $(this).data('colorPicker', plugin);
            }
        });
    };
})(jQuery);

/* Global state handler */

doodleJS.mod.stateHandler = (function ($, d) {
    var states = {};
    var initialHash = "";
    var needsValidation = false;
    var currentState = null;
    var handleDestruct = function (oldState) {
        states[(oldState || initialHash)].unload();
    };
    var goToInitial = function () {
        d.nav.navigateTo(initialHash);
    };
    var handleConstruct = function (newState) {
        var state = newState || initialHash;
        currentState = state;
        if (states[state]) {
            states[state].load(state);
        } else {
            goToInitial();
        }
    };
    var veto = function (oldHash, newHash, cbFn) {
        var state = oldHash || initialHash;
        if (!needsValidation || needsValidation(state, newHash)) {
            var validationFn = states[state].validate;
            if (validationFn) {
                try {
                    validationFn(function (valid) {
                        cbFn(!valid);
                    });
                } catch (err) {
                    d.utils.log("error in veto-fn: " + err);
                    cbFn(true);
                }
            } else {
                cbFn(false);
            }
        } else {
            cbFn(false);
        }
    };
    return {
        init: function (pStates, initHash, pNeedsValidation) {
            states = pStates;
            initialHash = initHash;
            needsValidation = pNeedsValidation;
            $.each(states, function (state, stateObject) {
                if ($.isArray(stateObject.hash)) {
                    var i;
                    var len;
                    for (i = 0, len = stateObject.hash.length; i < len; i++) {
                        states[stateObject.hash[i]] = stateObject;
                    }
                } else {
                    states[stateObject.hash] = stateObject;
                }
                stateObject.init();
            });

            d.utils.subscribeTo("navigateDestruct", handleDestruct);
            d.utils.subscribeTo("navigateConstruct", handleConstruct);
            d.nav.setVetoFn(veto);
            if (!d.utils.rescue.hash) {
                d.nav.navigateTo();
            }
        },
        getState: function (hash) {
            return states[hash || initialHash];
        },
        // need only in meetme participate refactor?
        getCurrentState: function () {
            return currentState;
        },
        goToInitial: goToInitial,
        veto: veto,
        handleDestruct: handleDestruct,
        handleConstruct: handleConstruct
    };
})(jQuery, d);

d.stateHandler = doodleJS.mod.stateHandler;

d.utils.messaging = {};

d.utils.messaging.personalMessage = function (textareaId, text, templates, selected, l10n, cannotChooseTemplate) {
    var form = $('<div/>');
    var templHead = $('<h3 class="spaceBBefore spaceDAfter" style="width: 722px;"/>').text(l10n.message);
    var selectTemplate = $('<select id="templateKind" class="rescueData" name="templateKind" style="float:right"/>');
    $.each(templates, function (i, template) {
        selectTemplate.append($('<option/>').val(template.value).text(template.title));
    });
    var automessage = $('<h4 class="spaceCAfter" id="autoMessage"/>');
    selectTemplate.val(selected).change(function () {
        $.each(templates, function (i, temp) {
            if (temp.value === selectTemplate.val()) {
                automessage.html(temp.html);
            }
        });
    }).trigger("change");
    if (templates.length <= 1 || cannotChooseTemplate) {
        selectTemplate.hide();
    }
    templHead.append($('<span style="font-size: 12px; font-weight: normal"/>').append(selectTemplate));
    form.append(templHead);
    var allShaddow = $('<div id="invShaddow"/>').append($('<div id="invShdTopLeft"/>')).append($('<div id="invShdTop"/>')).append($('<div id="invShdTopRight"/>')).append($('<div id="invShdLeft"/>'));
    var invDiv = $('<div id="invConMessage"/>');
    invDiv.append(automessage);
    invDiv.append($('<textarea class="rescueData" name="personalMessageTxt" cols="70" rows="6"/>').val(text || "").hint(l10n.typeHerePersonalMessage).attr("id", textareaId));
    invDiv.append($('<p class="error"/>').attr("id", textareaId + "Error").hide());
    invDiv.append($('<label class="hiddenAcc"/>').attr("for", textareaId).text(l10n.message));
    allShaddow.append(invDiv);
    allShaddow.append($('<div id="invShdRight"/>'));
    var shaddow = $('<div id="invShaddowBottom" class="clearfix"/>').append($('<div id="msgshwleft"/>')).append($('<div id="msgshwmid"/>')).append($('<div id="msgshwright"/>'));
    allShaddow.append($('<div id="invShdLeftBottom"/>')).append(shaddow).append($('<div id="invShdRightBottom"/>'));
    form.append(allShaddow);
    if (!(d.myDoodle.mandator.features.hideAds || (d.poll && d.poll.features && d.poll.features.hideAds))) {
        form.append($('<p class="grey"/>').append($('<span/>').text(l10n.doodleAdRemark + " ")).append($('<span/>').text(l10n.premiumEmailsAdsFree + " ").append($('<a href="/premium/plans.html?linkSource=PersonalMessage"/>').text(l10n.learnMoreMain + "..."))));
    }
    return form;
};

d.utils.messaging.addresses = function (addressesId, addressgroups, selected, l10n, beforeTextFieldNode) {
    var addressesNode = $('<textarea rows="4" class="spaceEAfter rescueData" style="width: 530px;"/>').attr("id", addressesId).attr("name", addressesId).hint(l10n.startTypingEmails);
    var addressesLabel = $('<label class="hiddenAcc"/>').attr("for", addressesId).text(l10n.emailAddresses);
    var abookNode = $('<span class="h4 abook busy">&nbsp;</span>');
    d.dyn.needUiBloat(function () {
        var params = {
            token: d.myDoodle.token
        };
        if (d.myDoodle.user.addressBooksReadyForQuery && d.myDoodle.user.addressbook === undefined) {
            $.get("/np/users/me/contacts?" + $.param(params), function (response) {
                d.myDoodle.user.addressbook = response.contacts;
                d.myDoodle.user.addressbookStatus = response.status;
                d.utils.messaging._initAddressbook(addressesNode, abookNode, l10n);
            });
        } else {
            d.utils.messaging._initAddressbook(addressesNode, abookNode, l10n);
        }
    });
    var form = $('<div/>');
    form.append($('<h3 class="spaceEAfter"/>').text(l10n.emailAddresses));
    var abook = $('<p class="spaceDAfter" style="width: 539px;"/>').append($('<span class="h4"/>').text(l10n.addressBooks + ": ")).append(abookNode);
    form.append(abook);
    form.append(beforeTextFieldNode);

    var select = $('<select id="contactType" class="rescueData" style="float:right"/>');
    $.each(addressgroups, function (i, addresses) {
        select.append($('<option/>').val(addresses.value).text(addresses.title));
    });
    if (addressgroups.length <= 1) {
        select.hide();
    }
    select.val(selected).change(function () {
        $.each(addressgroups, function (i, add) {
            if (add.value === select.val()) {
                $("#" + addressesId).val(add.emails);
            }
        });
    }).trigger("change");
    $.each(addressgroups, function (i, add) {
        if (add.value === select.val()) {
            addressesNode.nhVal(add.emails);
        }
    });
    if (!(d.myDoodle.mandator.features.quickReply || (d.poll && d.poll.features && d.poll.features.quickReply))) {
        select.attr("selectedIndex", 0);
        select.children().each(function (i, val) {
            if (i > 0) {
                $(this).attr("disabled", true);
            }
        });
        select.addPremiumNag();
    }
    abook.append(select);

    form.appendAll(addressesNode, addressesLabel);
    form.append($('<p class="grey spaceEBefore spaceEAfter"/>').text(l10n.multipleEmailSeparatorRemark));
    form.append($('<p class="error"/>').attr("id", addressesId + "Error").hide());

    return form;
};

d.utils.messaging._initAddressbook = function (addressesNode, abookNode, l10n) {
    var formContacts = [];
    if (d.myDoodle.user.addressBooksReadyForQuery) {
        $.each(d.myDoodle.user.addressbook, function (index, value) {
            if (value !== undefined) {
                formContacts[index] = {};
                formContacts[index].name = value.name;
                formContacts[index].email = value.email;
                formContacts[index].contacts = value.contacts;
            }
        });
        addressesNode.bind("keydown", function (event) {
            if (event.keyCode === $.ui.keyCode.TAB && $(this).data("autocomplete").menu.active) {
                event.preventDefault();
            }
        }).autocomplete({
            minLength: 2,
            source: function (request, response) {
                var term = d.utils.messaging._findTerm(request.term);
                response($.map(d.utils.messaging._match(formContacts, term), function (contact) {
                    return {
                        label: d.utils.messaging._formatItem(contact),
                        value: contact,
                        highlight: term
                    };
                }));
            },
            focus: function () {
                return false;
            },
            select: function (event, ui) {
                kiss.usedContactAutocomplete = true;
                var terms = d.utils.messaging._split(this.value);
                terms.pop();
                terms.push(d.utils.messaging._formatResult(ui.item.value));
                terms.push("");
                this.value = terms.join(", ");
                return false;
            }
        });

        if (d.myDoodle.user.addressbookStatus !== "INVALID") {
            abookNode.text(l10n.connected);
        } else {
            var link = $('<a id="brokenAdbLnk" href="/mydoodle/manageAccount.html#calendars" target="_blank"/>');
            var icon = $('<span/>').addClass('errorIcon').html('&nbsp;&nbsp;&nbsp;&nbsp;');
            icon.css('width', '14px');
            icon.css('height', '14px');
            icon.css('display', 'inline-block');
            icon.css('margin-top', '2px');
            var errorMsg = l10n.addressbooksDoNotWork + " " + l10n.clickForMoreDetails;
            var content = $('<span/>').text(l10n.connected);
            abookNode.css('display', 'inline-block');
            abookNode.attr('title', errorMsg).append(link.append(content).append(icon));
        }

    } else {
        abookNode.append($('<a href="/mydoodle/manageAccount.html#calendars" target="_blank"/>').text(l10n.connect));
    }
    abookNode.removeClass("busy");
};

d.utils.messaging._findTerm = function (string) {
    string = $.trim(string);
    var commaPos = string.lastIndexOf(",");
    if (commaPos > -1) {
        var term = $.trim(string.substring(commaPos + 1, string.length));
        return term;
    }
    return $.trim(string);
};

d.utils.messaging._match = function (contacts, search) {
    var term = $.grep(contacts, function (contact) {
        return (contact.email + " " + contact.name).toLowerCase().indexOf($.trim(search.toLowerCase())) > -1;
    });
    return term;
};

d.utils.messaging._split = function (val) {
    return val.split(/,\s*/);
};
d.utils.messaging._extractLast = function (term) {
    return d.utils.messaging._split(term).pop();
};

d.utils.messaging._formatItem = function (row) {
    if (row.name !== "") {
        if (row.contacts) {
            var emails = [];
            $.each(row.contacts, function (i, val) {
                emails.push(val.email);
            });
            var contacts = d.utils.join2(emails, ", ", ", ");
            if (contacts.length > 50) {
                contacts = contacts.substr(0, 50) + "...";
            }
            return d.utils.encode("" + row.name + " (" + contacts + ")");
        } else {
            return d.utils.encode("" + row.name + " (" + row.email + ")");
        }
    } else {
        return d.utils.encode(row.email);
    }
};

d.utils.messaging._formatResult = function (row) {
    if (row.name !== "") {
        if (row.contacts) {
            var contacts = [];
            $.each(row.contacts, function (i, val) {
                contacts.push((val.name ? val.name : "") + " <" + val.email + ">");
            });
            return d.utils.join2(contacts, ", ", ", ");
        } else {
            return "" + row.name + " <" + row.email + ">";
        }
    } else {
        return row.email;
    }
};

/* Ads */

d.ads = {};
d.ads.align = function () {
    $("body").addClass("fireplace");
};
d.ads.adtechs = [];
d.ads.impressAds = function () {
    if (typeof (googletag) !== 'undefined' && typeof (googletag.pubads) !== 'undefined') { // DFP / crunching
        googletag.pubads().refresh();
    }

    if (typeof (window.adgroupid) !== 'undefined') { // Adtech / Switzerland
        window.adgroupid = Math.round(Math.random() * 1000);
        $.each(d.ads.adtechs, function (i, adtech) {
            adtech();
        });
    }
};

d.ads.count = function (placement, keywords) {
    if (!keywords) {
        return;
    }
    if (d.poll && d.poll.id && /^BSP/.test(d.poll.id)) {
        // ignore example polls
        return;
    }
    var type;
    if (placement === "skyscraper") {
        type = "BRANDING";
    } else if (placement === "rectangle") {
        type = "RECTANGLE";
    } else if (placement === "MOBILE_BANNER") {
        type = "MOBILE_BANNER";
    } else {
        // skyscrapers & background do NOT get tracked
        return;
    }
    // type can be: BRANDING, RECTANGLE, MOBILE_BANNER;
    var params = {
        locale: d.myDoodle.viewLocale,
        type: type,
        keywords: keywords,
        v: Math.random()
    };
    $.get("/np/ads/count?" + $.param(params));
};

d.calAds = {};

d.calAds.clickUrl = function (creative) {
    d.calAds.openUrl(creative, "url");
};

d.calAds.clickMapUrl = function (creative) {
    d.calAds.openUrl(creative, "mapurl");
};

d.calAds.openUrl = function (creative, endpoint) {
    var url = "/np/wersys/creatives/" + encodeURIComponent(creative.id) + '/' + encodeURIComponent(endpoint) + '?rand=' + encodeURIComponent(parseInt(Math.random() * 99999999, 10));// cache buster
    window.open(url);
};

d.calAds._trackingUrl = {
    "show": "/np/wersys/creatives/impressions",
    "hover": "/np/wersys/creatives/hoverImpressions"
};

d.calAds.trackImpressions = function (kind, creatives, from) {
    var impressions = [];
    $.each(creatives, function (i, creative) {
        impressions.push(creative.id);
        if (kind === "hover") {
            var calAdsTrackingUrl = d.utils.calAdsTrackingUrl + "?calAd=" + encodeURIComponent(creative.id);
            var calAdsTrackingFrame = document.getElementById("calAdsTracking");
            if (calAdsTrackingFrame !== null) {
                calAdsTrackingFrame.src = calAdsTrackingUrl;
            }
        }
    });

    if (impressions.length > 0) {
        var params = {
            ids: impressions,
            from: from,
            rand: encodeURIComponent(parseInt(Math.random() * 99999999, 10))
        };
        $.get(d.calAds._trackingUrl[kind] + "?" + $.param(params));
    }
};

d.calAds.appendQtip = function (node, creative, from) {
    var qContent = $('<div/>');
    qContent.append($('<div id="hoverTrackingPixel/>'));// .css('display', 'none'));
    qContent.append($('<p class="adLabel"/>').text(d.l10n.advertisement));
    qContent.append($('<p class="adTitle"/>').html(creative.titleHTML));
    qContent.append($('<p class="adDate"/>').text(creative.dateStr));
    if (!creative.preview) {
        qContent.append($('<a href="#"/>').click(function () {
            d.calAds.clickUrl(creative);
            return false;
        }).append($('<div style="text-align:center"/>').append($('<img class="adImage"/>').attr("src", creative.imagePath))));
    } else if (creative.image) {
        qContent.append($('<div style="text-align:center"/>').append($('<img class="adImage"/>').css("width", creative.imageWidth).css("height", creative.imageHeight).attr("src", creative.image)));
    } else if (creative.imagePath) {
        qContent.append($('<div style="text-align:center"/>').append($('<img class="adImage"/>').attr("src", creative.imagePath)));
    } else if (creative.imageHTML) {
        qContent.append($('<div class="adImage"/>').html(creative.imageHTML));
    }
    qContent.append($('<p class="adDescription"/>').html(creative.descriptionHTML));
    var blueButton = d.ui.generateMediumButton(d.l10n.learnMoreMain).click(function () {
        if (!creative.preview) {
            d.calAds.clickUrl(creative);
        }
        return false;
    });
    qContent.append($('<div class="adButton clearfix"/>').append(blueButton));
    if (creative.location) {
        var location = $('<p class="adLocation"/>').append($('<span/>').html(creative.locationHTML)).append($('<span/>').text(" - ").append($('<a href="#"/>').click(function () {
            if (!creative.preview) {
                d.calAds.clickMapUrl(creative);
            }
            return false;
        }).text(d.l10n.map)));
        qContent.append(location);
    }

    node.dtip({
        content: qContent,
        anchor: 's',
        delay: creative.preview ? 0 : undefined,
        styleClass: "adBubble",
        anchorWidth: 8,
        anchorHeight: 16,
        show: function () {
            if (!creative.preview) {
                d.calAds.trackImpressions("hover", [creative], from);
            }
        }
    });
};

/* KISS Metrics */

kiss.BrowserDetect = {
    init: function () {
        this.browser = this.searchString(this.dataBrowser) || "An unknown browser";
        this.version = this.searchVersion(navigator.userAgent) || this.searchVersion(navigator.appVersion) || "an unknown version";
        this.OS = this.searchString(this.dataOS) || "an unknown OS";
    },
    searchString: function (data) {
        var i;
        for (i = 0; i < data.length; i++) {
            var dataString = data[i].string;
            var dataProp = data[i].prop;
            this.versionSearchString = data[i].versionSearch || data[i].identity;
            if (dataString) {
                if (dataString.indexOf(data[i].subString) !== -1) {
                    return data[i].identity;
                }
            } else if (dataProp) {
                return data[i].identity;
            }
        }
    },
    searchVersion: function (dataString) {
        var index = dataString.indexOf(this.versionSearchString);
        if (index === -1) {
            return;
        }
        return parseFloat(dataString.substring(index + this.versionSearchString.length + 1));
    },
    dataBrowser: [{
        string: navigator.userAgent,
        subString: "Chrome",
        identity: "Chrome"
    }, {
        string: navigator.vendor,
        subString: "Apple",
        identity: "Safari",
        versionSearch: "Version"
    }, {
        prop: window.opera,
        identity: "Opera",
        versionSearch: "Version"
    }, {
        string: navigator.userAgent,
        subString: "Firefox",
        identity: "Firefox"
    }, {
        string: navigator.userAgent,
        subString: "MSIE",
        identity: "Explorer",
        versionSearch: "MSIE"
    }, {
        string: navigator.userAgent,
        subString: "Gecko",
        identity: "Mozilla",
        versionSearch: "rv"
    }],
    dataOS: [{
        string: navigator.platform,
        subString: "Win",
        identity: "Windows"
    }, {
        string: navigator.platform,
        subString: "Mac",
        identity: "Mac"
    }, {
        string: navigator.userAgent,
        subString: "iPhone",
        identity: "iPhone/iPod"
    }, {
        string: navigator.platform,
        subString: "Linux",
        identity: "Linux"
    }]

};

kiss.init = function () {
    if (!kiss.initialized) {
        kiss.BrowserDetect.init();
        /*
         * $('.doodlead').click(function() { // Enable ad click tracking kiss.track("clicked on ad", { "ad location": $(this).attr('id') }); });
         */
        if (d.myDoodle.user && d.myDoodle.user.loggedIn && !kiss.tagged) {
            kiss.identify(d.myDoodle.user.id);
        }
        kiss.initialized = true;
    }
};

kiss.identify = function (userId) {
    if (window._kmq) {
        kiss.tagged = true;
        _kmq.push(['identify', userId]);
    }
};

kiss.track = function (event, eventData) {
    if (window._kmq) {
        kiss.init();
        var trackData = { // Augmenting of global data
            "Country": d.myDoodle.country,
            "Locale": d.myDoodle.viewLocale,
            "Browser": kiss.BrowserDetect.browser,
            "Browser Version": kiss.BrowserDetect.version,
            "Operating System": kiss.BrowserDetect.OS,
            "Screen Resolution": screen.width + "x" + screen.height,
            "IP Address": d.myDoodle.inetaddress,
            "User is logged in": d.myDoodle.user && d.myDoodle.user.loggedIn
        };

        if (d.myDoodle.user) {
            if (d.myDoodle.user.gender) {
                trackData = $.extend(trackData, {
                    "Gender": d.myDoodle.user.gender.toLowerCase()
                });
            }
            if (d.myDoodle.user.yearOfBirth) {
                trackData = $.extend(trackData, {
                    "Year of birth": d.myDoodle.user.yearOfBirth
                });
            }
        }

        if (d.myDoodle.mandator && d.myDoodle.mandator.isMandator) {
            trackData = $.extend(trackData, {
                "Mandator ID": d.myDoodle.mandator.id
            });
        }

        if (typeof window.innerWidth !== 'undefined') {
            trackData = $.extend(trackData, {
                "ViewPort": window.innerWidth + "x" + window.innerHeight
            });
        }

        if (window.performance && performance.timing) {
            trackData = $.extend(trackData, {
                "onLoadEventTime": performance.timing.loadEventEnd - performance.timing.navigationStart
            });
        }

        var urlVars = d.utils.getUrlVars();
        if (urlVars.linkSource) {
            trackData = $.extend(trackData, {
                "Link Source": urlVars.linkSource
            });
        }
        if (urlVars.signupSource) {
            trackData = $.extend(trackData, {
                "SignUp Source": urlVars.signupSource
            });
        }

        _kmq.push(['record', event, $.extend(trackData, eventData)]);
    }
};

/* Wirtualna Polska */

d.wp = {};

d.wp.isWpPl = function () {
    return Boolean(d.myDoodle.mandator.isWpPl);
};

d.wp.composeUrl = function (poll) {
    var params = {
        partner: "doodle",
        sharedHash: poll.id,
        pollTitle: poll.title,
        pollEvent: (poll.state === "CLOSED") ? "closed" : "created",
        wpSubject: poll.wpSubject,
        wpMessage: poll.wpMessage
    };
    if (poll.state === "OPEN") {
        params.adminHash = poll.adminKey;
    }
    return d.myDoodle.mandator.wpUrl + "?" + $.param(params);
};

d.wp.openWindow = function (url) {
    return window.open(url, "ddl2wp", "width=750&height=645");
};

/*
 * ! jquery.scrolldepth.js | v0.1.1 Copyright (c) 2012 Rob Flaherty (@robflaherty) Licensed under the MIT and GPL licenses.
 */
(function (d, c, a, g) {
    var e = {
        elements: [],
        minHeight: 0,
        offset: 0,
        percentage: true,
        testing: false
    }, f = d(c), b = [];
    d.scrollDepth = function (i) {
        i = d.extend({}, e, i);
        if (d(a).height() < i.minHeight) {
            return;
        }
        function l (n, m) {
            if (!i.testing) {
                _gaq.push(["_trackEvent", "Scroll Depth", n, m, 1, true]);
            } else {
                d("#console").html(n + ": " + m);
            }
        }
        l("Percentage", "Baseline");
        function k (m) {
            return {
                "25%": parseInt(m * 0.25, 10),
                "50%": parseInt(m * 0.5, 10),
                "75%": parseInt(m * 0.75, 10),
                "100%": m - 1
            };
        }
        function h (n, m) {
            d.each(n, function (o, p) {
                if (d.inArray(o, b) === -1 && m >= p) {
                    l("Percentage", o);
                    b.push(o);
                }
            });
        }
        function j (n, m) {
            d.each(n, function (o, p) {
                if (d.inArray(p, b) === -1 && d(p).length) {
                    if (m >= d(p).offset().top) {
                        l("Elements", p);
                        b.push(p);
                    }
                }
            });
        }
        f.on("scroll.scrollDepth", function () {
            var o = d(a).height(), n = c.innerHeight ? c.innerHeight : f.height(), m = f.scrollTop() + n, p = k(o);
            if (b.length >= 4 + i.elements.length) {
                f.off("scroll.scrollDepth");
                return;
            }
            if (i.elements) {
                j(i.elements, m);
            }
            if (i.percentage) {
                h(p, m);
            }
        });
    };
})(jQuery, window, document);