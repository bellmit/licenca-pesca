<template>
	<div :class="[
    type === 'textarea' ? 'el-textarea' : 'el-input',
    inputSize ? 'el-input--' + inputSize : '',
    {
      'is-disabled': inputDisabled,
      'el-input-group': $slots.prepend || $slots.append,
      'el-input-group--append': $slots.append,
      'el-input-group--prepend': $slots.prepend,
      'el-input--prefix': $slots.prefix || prefixIcon,
      'el-input--suffix': $slots.suffix || suffixIcon
    }
    ]"
	     @mouseenter="hovering = true"
	     @mouseleave="hovering = false"
	>
		<template v-if="type !== 'textarea'">
			<!-- 前置元素 -->
			<div class="el-input-group__prepend" v-if="$slots.prepend">
				<slot name="prepend"></slot>
			</div>
			<input
					:tabindex="tabindex"
					v-if="type !== 'textarea'"
					class="el-input__inner"
					v-bind="$attrs"
					:type="type"
					:disabled="inputDisabled"
					:autocomplete="autoComplete"
					:value="currentValue"
					ref="input"
					@input="handleInput"
					@focus="handleFocus"
					@blur="handleBlur"
					@change="handleChange"
					@keydown="handleKeydown"
					@keypress="handleKeypress"
					:aria-label="label"
			>
			<!-- 前置内容 -->
			<span class="el-input__prefix" v-if="$slots.prefix || prefixIcon" :style="prefixOffset">
        <slot name="prefix"></slot>
        <i class="el-input__icon"
           v-if="prefixIcon"
           :class="prefixIcon">
        </i>
      </span>
			<!-- 后置内容 -->
			<span
					class="el-input__suffix"
					v-if="$slots.suffix || suffixIcon || showClear || validateState && needStatusIcon"
					:style="suffixOffset">
        <span class="el-input__suffix-inner">
          <template v-if="!showClear">
            <slot name="suffix"></slot>
            <i class="el-input__icon"
               v-if="suffixIcon"
               :class="suffixIcon">
            </i>
          </template>
          <i v-else
             class="el-input__icon el-icon-circle-close el-input__clear"
             @click="clear"
          ></i>
        </span>
        <i class="el-input__icon"
           v-if="validateState"
           :class="['el-input__validateIcon', validateIcon]">
        </i>
      </span>
			<!-- 后置元素 -->
			<div class="el-input-group__append" v-if="$slots.append">
				<slot name="append"></slot>
			</div>
		</template>
		<textarea
				v-else
				:tabindex="tabindex"
				class="el-textarea__inner"
				:value="currentValue"
				@input="handleInput"
				ref="textarea"
				v-bind="$attrs"
				:disabled="inputDisabled"
				:style="textareaStyle"
				@focus="handleFocus"
				@blur="handleBlur"
				@change="handleChange"
				:aria-label="label"
		>
    </textarea>
	</div>
</template>
<script>
import emitter from "element-ui/src/mixins/emitter";
import Migrating from "element-ui/src/mixins/migrating";
import calcTextareaHeight from "../../utils/helpers/calcTextareaHeight";
import merge from "element-ui/src/utils/merge";
import mask from "../../utils/helpers/mask";

export default {
  name: "ElInput",
  componentName: "ElInput",
  mixins: [emitter, Migrating, mask],
  inheritAttrs: false,
  inject: {
    elForm: {
      default: ""
    },
    elFormItem: {
      default: ""
    }
  },
  data() {
    return {
      currentValue: this.value,
      textareaCalcStyle: {},
      prefixOffset: null,
      suffixOffset: null,
      hovering: false,
      focused: false
    };
  },
  props: {
    value: [String, Number],
    size: String,
    resize: String,
    form: String,
    disabled: Boolean,
    type: {
      type: String,
      default: "text"
    },
    autosize: {
      type: [Boolean, Object],
      default: false
    },
    autoComplete: {
      type: String,
      default: "off"
    },
    validateEvent: {
      type: Boolean,
      default: true
    },
    suffixIcon: String,
    prefixIcon: String,
    label: String,
    clearable: {
      type: Boolean,
      default: false
    },
    tabindex: String
  },
  computed: {
    _elFormItemSize() {
      return (this.elFormItem || {}).elFormItemSize;
    },
    validateState() {
      return this.elFormItem ? this.elFormItem.validateState : "";
    },
    needStatusIcon() {
      return this.elForm ? this.elForm.statusIcon : false;
    },
    validateIcon() {
      return {
        validating: "el-icon-loading",
        success: "el-icon-circle-check",
        error: "el-icon-circle-close"
      }[this.validateState];
    },
    textareaStyle() {
      return merge({}, this.textareaCalcStyle, { resize: this.resize });
    },
    inputSize() {
      return this.size || this._elFormItemSize || (this.$ELEMENT || {}).size;
    },
    inputDisabled() {
      return this.disabled || (this.elForm || {}).disabled;
    },
    isGroup() {
      return this.$slots.prepend || this.$slots.append;
    },
    showClear() {
      return (
        this.clearable &&
        !this.disabled &&
        this.currentValue !== "" &&
        (this.focused || this.hovering)
      );
    }
  },
  watch: {
    value(val, oldValue) {
      if (!this.maskOptions) {
        this.setCurrentValue(val);
      } else {
        this.maskBehaviour(val);
      }
    }
  },
  methods: {
    focus() {
      (this.$refs.input || this.$refs.textarea).focus();
    },
    blur() {
      (this.$refs.input || this.$refs.textarea).blur();
    },
    getMigratingConfig() {
      return {
        props: {
          icon: "icon is removed, use suffix-icon / prefix-icon instead.",
          "on-icon-click": "on-icon-click is removed."
        },
        events: {
          click: "click is removed."
        }
      };
    },
    handleBlur(event) {
      this.focused = false;
      this.$emit("blur", event);

      if (
        this.maskOptions &&
        this.maskOptions.clearIfNotMatch &&
        !this.maskState.regexMask.test(this.currentValue)
      ) {
        this.clear();
      }

      if (this.validateEvent) {
        this.dispatch("ElFormItem", "el.form.blur", [this.currentValue]);
      }
    },
    select() {
      (this.$refs.input || this.$refs.textarea).select();
    },
    resizeTextarea() {
      if (this.$isServer) return;
      const { autosize, type } = this;
      if (type !== "textarea") return;
      if (!autosize) {
        this.textareaCalcStyle = {
          minHeight: calcTextareaHeight(this.$refs.textarea).minHeight
        };
        return;
      }
      const minRows = autosize.minRows;
      const maxRows = autosize.maxRows;
      this.textareaCalcStyle = calcTextareaHeight(
        this.$refs.textarea,
        minRows,
        maxRows
      );
    },
    handleFocus(event) {
      this.focused = true;
      this.$emit("focus", event);
    },
    handleInput(event) {
      if (this.maskOptions && this.maskOptions.mask) {
        const value = this.maskBehaviour();

        if (value !== null) {
          this.$emit("input", value);
        }
      } else {
        const value = event.target.value;
        this.$emit("input", value);
        this.setCurrentValue(value);
      }
    },
    handleChange(event) {
      this.$emit("change", event.target.value);
    },

    handleKeydown(event) {
      this.maskState.keycode = event.keyCode || event.which;
      this.maskState.previusValue = this.currentValue;
      this.maskState.previusCaretPos = this.getCaret();
      this.maskState.maskDigitPosMapOld = this.maskState.maskDigitPosMap;
    },

    handleKeypress(event) {
      if (event.keyCode === 13) this.$emit("enter");
    },

    setCurrentValue(value) {
      if (value === this.currentValue) return;
      this.$nextTick(_ => {
        this.resizeTextarea();
      });
      this.currentValue = value;
      if (this.validateEvent) {
        this.dispatch("ElFormItem", "el.form.change", [value]);
      }
    },
    calcIconOffset(place) {
      const pendantMap = {
        suf: "append",
        pre: "prepend"
      };
      const pendant = pendantMap[place];
      if (this.$slots[pendant]) {
        return {
          transform: `translateX(${place === "suf" ? "-" : ""}${
            this.$el.querySelector(`.el-input-group__${pendant}`).offsetWidth
          }px)`
        };
      }
    },
    clear() {
      this.$emit("input", "");
      this.$emit("change", "");
      this.$emit("clear");
      this.setCurrentValue("");
      this.focus();
    }
  },
  created() {
    this.$on("inputSelect", this.select);
  },
  mounted() {
    this.resizeTextarea();
    if (this.isGroup) {
      this.prefixOffset = this.calcIconOffset("pre");
      this.suffixOffset = this.calcIconOffset("suf");
    }
  }
};
</script>
<style lang="sass">
.el-form-item.is-required:not(.is-no-asterisk) > .el-form-item__label:after
  content: '*';
  color: #f56c6c;
  margin-left: 4px;
.el-form-item__label:before 
  content: '' !important;

.el-button--primary

  background-color: #007EFE !important;
  border-color: #007EFE !important;

  &:hover
    background-color: #409EFF !important;
    border-color: #409EFF !important;

.el-button--primary.is-disabled
  background-color: #409EFF!important;
  border-color: #409EFF !important;
  color: white !important;
</style>
