<template lang="pug">
  #renovar-licenca
    h2 {{ $t(`${registrar_prefix}tituloRenovar`)}} - {{ $route.params.protocolo }}
    card
      el-steps(:active="step" :space="500" simple)
        el-step(:title="$t(`${registrar_prefix}steps.indices.informacoes`)" icon="el-icon-edit-outline")
        el-step(:title="$t(`${registrar_prefix}steps.indices.resumo`)" icon="el-icon-document")

      renovar-info-complementares(v-show="activeStep('INFORMACOES_COMPLEMENTARES')", ref="informacoesComplementaresStep")
      resumo-step(v-show="activeStep('RESUMO')", ref="resumo")


      #step-controller-renovacao
        .left
            el-button(icon="el-icon-arrow-left" type="primary" @click="prevStep" v-if="!activeStep('INFORMACOES_COMPLEMENTARES')") {{ $t(`${registrar_prefix}steps.botoes.voltar`) }}
            el-button(icon="el-icon-close" @click="cancelar") {{ $t(`${registrar_prefix}steps.botoes.cancelar`) }}
        .center
        .right
            el-button(icon="el-icon-check" type="primary" v-if="activeStep('RESUMO')" @click="concluir") {{ $t(`${registrar_prefix}steps.botoes.concluir`) }}
            el-button(icon="el-icon-arrow-right" type="primary" @click="nextStep" v-if="!activeStep('RESUMO')") {{ $t(`${registrar_prefix}steps.botoes.proxima`) }}



</template>

<script>
import { mapGetters } from "vuex";
import Card from "../layouts/Card";
import Properties from "../../properties";
import StepController from "../layouts/StepController";
import ResumoStep from "../business/resumo/ResumoStep";
import { translate } from "../../utils/helpers/internationalization";
import { REGISTRAR_GERAL_MESSAGES_PREFIX } from "../../utils/messages/interface/registrar/geral";
import RenovarInfoComplementares from "../business/informacoesComplementares/RenovarInformacoesComplementares";
import { FETCH_INFORMACAO_LICENCA,RENOVAR_CARTEIRA, CANCELAR_RENOVAR } from "../../store/actions.type";

export default {
  name: "RenovarLicenca",

  components: {
    ResumoStep,
    RenovarInfoComplementares,
    StepController,
    Card
  },

  data() {
      return {
        step: 1,
        registrar_prefix: REGISTRAR_GERAL_MESSAGES_PREFIX
      };
  },

  computed: {

    ...mapGetters(["showStepsController", "cadastroCanActive", "registroResource", "protocolo", "licenca"])
  },

  mounted(){
    this.$store.dispatch(FETCH_INFORMACAO_LICENCA, this.$route.params.protocolo);
  },

  methods: {
    activeStep(step) {
      const steps = Properties.STEPS;

      return this.step === steps[step];
    },
    nextStep() {
        if(this.activeStep('INFORMACOES_COMPLEMENTARES')) {
            if(this.checkValidationInformacoesComplementares()) {
              this.$refs.informacoesComplementaresStep.enviarParaStore();
              this.$refs.resumo.scroll();
              this.step++;
            }
        } else {
            if (this.step++ >= 2) this.step = 2;
        }
    },
    checkValidationInformacoesComplementares() {
      
      return this.$refs.informacoesComplementaresStep.validate();
    },
    prevStep() {
      if(this.activeStep('INFORMACOES_COMPLEMENTARES')) {
        
        this.$refs.identificationStep.prepararDados();
      }
      if (this.step-- <= 1) this.step = 1;
    },
    concluir() {
      this.$confirm(
        translate(`${this.registrar_prefix}confirm.mensagem`),
        translate(`${this.registrar_prefix}confirm.titulo`),
        {
          confirmButtonText: translate(
            `${this.registrar_prefix}confirm.botoes.confirm`
          ),
          cancelButtonText: translate(
            `${this.registrar_prefix}confirm.botoes.cancel`
          )
        }
      )
        .then(() => {
          let registro = this.registroResource;
          let date = this.registroResource.solicitante.dataNascimento;
          if(typeof(date) !== "string"){

            registro.solicitante.dataNascimento =  date.getDate() + '/' + (date.getMonth() + 1)+ '/' + date.getFullYear();
          }
          registro.protocolo = this.$route.params.protocolo;
          this.$store.dispatch(RENOVAR_CARTEIRA, registro).then(p => {
            let protocolo = p.replace("/", "").replace("-", "").replace("-", "");
            this.$router.push({
              name: "consultar",
              params: { protocolo: protocolo }
            });
          });
        })
        .catch(() => {
          // DO nothing!
        });
    },

    cancelar() {
      this.$confirm(
        translate(`${this.registrar_prefix}cancel.mensagemRenovar`),
        translate(`${this.registrar_prefix}cancel.titulo`),
        {
          confirmButtonText: translate(
            `${this.registrar_prefix}cancel.botoes.confirm`
          ),
          cancelButtonText: translate(
            `${this.registrar_prefix}cancel.botoes.cancel`
          )
        }
      )
          .then(() => {
            this.$router.push({name: 'home'});
          })
          .catch(() => {
            // DO nothing!
          });
    }
  }
};
</script>

<style lang="sass">
@import "../../theme/tools/variables"

#step-controller-renovacao
    margin-top: 30px
    border-top: $--cor-borda 1px solid
    padding-top: 20px
    display: flex
    align-items: center
    justify-content: space-between

</style>
