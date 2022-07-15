package com.mercadopago.android.px.internal.di;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import com.mercadopago.android.px.addons.BehaviourProvider;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.addons.model.SecurityValidationData;
import com.mercadopago.android.px.configuration.AdvancedConfiguration;
import com.mercadopago.android.px.configuration.PaymentConfiguration;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.core.SplitPaymentProcessor;
import com.mercadopago.android.px.core.internal.MercadoPagoCardStorage;
import com.mercadopago.android.px.internal.configuration.InternalConfiguration;
import com.mercadopago.android.px.internal.core.ApplicationModule;
import com.mercadopago.android.px.internal.datasource.AmountConfigurationRepositoryImpl;
import com.mercadopago.android.px.internal.datasource.AmountService;
import com.mercadopago.android.px.internal.datasource.BankDealsService;
import com.mercadopago.android.px.internal.datasource.CardTokenService;
import com.mercadopago.android.px.internal.datasource.CongratsRepositoryImpl;
import com.mercadopago.android.px.internal.datasource.DiscountServiceImp;
import com.mercadopago.android.px.internal.datasource.EscPaymentManagerImp;
import com.mercadopago.android.px.internal.datasource.ExperimentsService;
import com.mercadopago.android.px.internal.datasource.IdentificationService;
import com.mercadopago.android.px.internal.datasource.InitService;
import com.mercadopago.android.px.internal.datasource.InstructionsService;
import com.mercadopago.android.px.internal.datasource.IssuersServiceImp;
import com.mercadopago.android.px.internal.datasource.PaymentMethodsService;
import com.mercadopago.android.px.internal.datasource.PaymentService;
import com.mercadopago.android.px.internal.datasource.PluginService;
import com.mercadopago.android.px.internal.datasource.PrefetchInitService;
import com.mercadopago.android.px.internal.datasource.SummaryAmountService;
import com.mercadopago.android.px.internal.datasource.TokenizeService;
import com.mercadopago.android.px.internal.datasource.cache.Cache;
import com.mercadopago.android.px.internal.datasource.cache.InitCacheCoordinator;
import com.mercadopago.android.px.internal.datasource.cache.InitDiskCache;
import com.mercadopago.android.px.internal.datasource.cache.InitMemCache;
import com.mercadopago.android.px.internal.repository.AmountConfigurationRepository;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.BankDealsRepository;
import com.mercadopago.android.px.internal.repository.CardTokenRepository;
import com.mercadopago.android.px.internal.repository.CongratsRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.EscPaymentManager;
import com.mercadopago.android.px.internal.repository.ExperimentsRepository;
import com.mercadopago.android.px.internal.repository.IdentificationRepository;
import com.mercadopago.android.px.internal.repository.InitRepository;
import com.mercadopago.android.px.internal.repository.InstructionsRepository;
import com.mercadopago.android.px.internal.repository.IssuersRepository;
import com.mercadopago.android.px.internal.repository.PaymentMethodsRepository;
import com.mercadopago.android.px.internal.repository.PaymentRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.PluginRepository;
import com.mercadopago.android.px.internal.repository.SummaryAmountRepository;
import com.mercadopago.android.px.internal.repository.TokenRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.services.BankDealService;
import com.mercadopago.android.px.internal.services.CheckoutService;
import com.mercadopago.android.px.internal.services.CongratsService;
import com.mercadopago.android.px.internal.services.GatewayService;
import com.mercadopago.android.px.internal.services.InstallmentService;
import com.mercadopago.android.px.internal.services.InstructionsClient;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.model.Device;
import com.mercadopago.android.px.model.internal.InitResponse;
import com.mercadopago.android.px.services.MercadoPagoServices;
import com.mercadopago.android.px.tracking.internal.MPTracker;

import static com.mercadopago.android.px.internal.util.MercadoPagoUtil.getPlatform;

public final class Session extends ApplicationModule implements AmountComponent {

    /**
     * This singleton instance is safe because session will work with application applicationContext. Application
     * applicationContext it's never leaking.
     */
    @SuppressLint("StaticFieldLeak")
    private static Session instance;

    private boolean initialized = false;

    // mem cache - lazy init.
    private ConfigurationModule configurationModule;
    private DiscountRepository discountRepository;
    private AmountRepository amountRepository;
    private InitRepository initRepository;
    private PaymentRepository paymentRepository;
    private AmountConfigurationRepository amountConfigurationRepository;
    private Cache<InitResponse> initCache;
    private PluginService pluginRepository;
    private InternalConfiguration internalConfiguration;
    private InstructionsService instructionsRepository;
    private SummaryAmountRepository summaryAmountRepository;
    private IssuersRepository issuersRepository;
    private CardTokenRepository cardTokenRepository;
    private BankDealsRepository bankDealsRepository;
    private IdentificationRepository identificationRepository;
    private PaymentMethodsRepository paymentMethodsRepository;
    private CongratsRepository congratsRepository;
    private ExperimentsRepository experimentsRepository;
    private EscPaymentManagerImp escPaymentManager;
    private ViewModelModule viewModelModule;
    private final NetworkModule networkModule;

    private Session(@NonNull final Context context, @NonNull final NetworkModule networkModule) {
        super(context);
        this.networkModule = networkModule;
    }

    public static Session getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                "Session is not initialized. Make sure to call Session.initialize(Context) first.");
        }
        return instance;
    }

    public static Session initialize(@NonNull final Context context, @NonNull final NetworkModule networkModule) {
        // In shared processes' content providers getApplicationContext() can return null.
        instance = new Session(context.getApplicationContext() == null ? context : context.getApplicationContext(),
            networkModule);
        return instance;
    }

    /**
     * Initialize Session with MercadoPagoCheckout information.
     *
     * @param mercadoPagoCheckout non mutable checkout intent.
     */
    public void init(@NonNull final MercadoPagoCheckout mercadoPagoCheckout) {
        //TODO add session mapping object.
        // delete old data.
        clear();

        initIds(mercadoPagoCheckout);

        // Store persistent paymentSetting
        final ConfigurationModule configurationModule = getConfigurationModule();

        final PaymentConfiguration paymentConfiguration = mercadoPagoCheckout.getPaymentConfiguration();
        final PaymentSettingRepository paymentSetting = configurationModule.getPaymentSettings();
        paymentSetting.configure(mercadoPagoCheckout.getPublicKey());
        paymentSetting.configure(mercadoPagoCheckout.getAdvancedConfiguration());
        paymentSetting.configurePrivateKey(mercadoPagoCheckout.getPrivateKey());
        paymentSetting.configure(paymentConfiguration);
        resolvePreference(mercadoPagoCheckout, paymentSetting);
        // end Store persistent paymentSetting

        initialized = true;
    }

    public void init(@NonNull final MercadoPagoCardStorage mercadoPagoCardStorage) {
        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }

    private void resolvePreference(@NonNull final MercadoPagoCheckout mercadoPagoCheckout,
        final PaymentSettingRepository paymentSetting) {
        final String preferenceId = mercadoPagoCheckout.getPreferenceId();

        if (TextUtil.isEmpty(preferenceId)) {
            paymentSetting.configure(mercadoPagoCheckout.getCheckoutPreference());
        } else {
            //Pref cerrada.
            paymentSetting.configurePreferenceId(preferenceId);
        }
    }

    private void clear() {
        getExperimentsRepository().reset();
        getConfigurationModule().reset();
        getInitCache().evict();
        configurationModule = null;
        discountRepository = null;
        amountRepository = null;
        initRepository = null;
        paymentRepository = null;
        initCache = null;
        pluginRepository = null;
        internalConfiguration = null;
        instructionsRepository = null;
        summaryAmountRepository = null;
        amountConfigurationRepository = null;
        issuersRepository = null;
        cardTokenRepository = null;
        paymentMethodsRepository = null;
        congratsRepository = null;
        escPaymentManager = null;
        viewModelModule = null;
        networkModule.clear();
        initialized = false;
    }

    public InitRepository getInitRepository() {
        if (initRepository == null) {
            final PaymentSettingRepository paymentSettings = getConfigurationModule().getPaymentSettings();
            initRepository = new InitService(paymentSettings, getExperimentsRepository(),
                configurationModule.getDisabledPaymentMethodRepository(), getMercadoPagoESC(),
                networkModule.getRetrofitClient().create(CheckoutService.class),
                networkModule.getFlowIdProvider(), getInitCache());
        }
        return initRepository;
    }

    public ExperimentsRepository getExperimentsRepository() {
        if (experimentsRepository == null) {
            experimentsRepository = new ExperimentsService(getSharedPreferences());
        }

        return experimentsRepository;
    }

    public SummaryAmountRepository getSummaryAmountRepository() {
        if (summaryAmountRepository == null) {
            final PaymentSettingRepository paymentSettings = getConfigurationModule().getPaymentSettings();
            final AdvancedConfiguration advancedConfiguration = paymentSettings.getAdvancedConfiguration();
            final UserSelectionRepository userSelectionRepository =
                getConfigurationModule().getUserSelectionRepository();
            final InstallmentService paymentService =
                networkModule.getRetrofitClient().create(InstallmentService.class);

            summaryAmountRepository = new SummaryAmountService(paymentService, paymentSettings,
                advancedConfiguration, userSelectionRepository, networkModule.getProductIdProvider());
        }
        return summaryAmountRepository;
    }

    @NonNull
    public ESCManagerBehaviour getMercadoPagoESC() {
        //noinspection ConstantConditions
        return BehaviourProvider
            .getEscManagerBehaviour(networkModule.getSessionIdProvider().getSessionId(),
                networkModule.getFlowIdProvider().getFlowId());
    }

    @NonNull
    private Device getDevice() {
        return new Device(getApplicationContext());
    }

    @NonNull
    public MercadoPagoServices getMercadoPagoServices() {
        final PaymentSettingRepository paymentSettings = getConfigurationModule().getPaymentSettings();
        return new MercadoPagoServices(getApplicationContext(), paymentSettings.getPublicKey(),
            paymentSettings.getPrivateKey());
    }

    @Override
    public AmountRepository getAmountRepository() {
        if (amountRepository == null) {
            final ConfigurationModule configurationModule = getConfigurationModule();
            final PaymentSettingRepository configuration = configurationModule.getPaymentSettings();
            amountRepository = new AmountService(configuration,
                configurationModule.getChargeSolver(),
                getDiscountRepository());
        }
        return amountRepository;
    }

    @NonNull
    public DiscountRepository getDiscountRepository() {
        if (discountRepository == null) {
            discountRepository =
                new DiscountServiceImp(getInitRepository(), getConfigurationModule().getUserSelectionRepository());
        }
        return discountRepository;
    }

    @NonNull
    public AmountConfigurationRepository getAmountConfigurationRepository() {
        if (amountConfigurationRepository == null) {
            amountConfigurationRepository =
                new AmountConfigurationRepositoryImpl(getInitRepository(),
                    getConfigurationModule().getUserSelectionRepository());
        }
        return amountConfigurationRepository;
    }

    @NonNull
    public ConfigurationModule getConfigurationModule() {
        if (configurationModule == null) {
            configurationModule = new ConfigurationModule(getApplicationContext());
        }
        return configurationModule;
    }

    @NonNull
    private Cache<InitResponse> getInitCache() {
        if (initCache == null) {
            initCache =
                new InitCacheCoordinator(new InitDiskCache(getFileManager()), new InitMemCache());
        }
        return initCache;
    }

    @NonNull
    public PluginRepository getPluginRepository() {
        if (pluginRepository == null) {
            pluginRepository =
                new PluginService(getApplicationContext(), getConfigurationModule().getPaymentSettings());
        }
        return pluginRepository;
    }

    @NonNull
    public PaymentRepository getPaymentRepository() {
        if (paymentRepository == null) {
            final ConfigurationModule configurationModule = getConfigurationModule();
            final SplitPaymentProcessor paymentProcessor =
                getConfigurationModule().getPaymentSettings().getPaymentConfiguration().getPaymentProcessor();
            paymentRepository = new PaymentService(configurationModule.getUserSelectionRepository(),
                configurationModule.getPaymentSettings(),
                configurationModule.getDisabledPaymentMethodRepository(),
                getPluginRepository(),
                getDiscountRepository(),
                getAmountRepository(),
                paymentProcessor,
                getApplicationContext(),
                getEscPaymentManager(),
                getMercadoPagoESC(),
                getTokenRepository(),
                getInstructionsRepository(),
                getInitRepository(),
                getAmountConfigurationRepository(),
                getCongratsRepository());
        }

        return paymentRepository;
    }

    @NonNull
    public EscPaymentManager getEscPaymentManager() {
        if (escPaymentManager == null) {
            escPaymentManager =
                new EscPaymentManagerImp(getMercadoPagoESC(), getConfigurationModule().getPaymentSettings());
        }
        return escPaymentManager;
    }

    @NonNull
    private TokenRepository getTokenRepository() {
        return new TokenizeService(networkModule.getRetrofitClient().create(GatewayService.class),
            getConfigurationModule().getPaymentSettings(), getMercadoPagoESC(), getDevice());
    }

    @NonNull
    public InternalConfiguration getInternalConfiguration() {
        return internalConfiguration == null ? new InternalConfiguration(false) : internalConfiguration;
    }

    /**
     * Set internal configuration after building MercadoPagoCheckout.
     *
     * @param internalConfiguration internal configuration for checkout.
     */
    @SuppressWarnings("unused")
    public void setInternalConfiguration(@NonNull final InternalConfiguration internalConfiguration) {
        this.internalConfiguration = internalConfiguration;
    }

    @NonNull
    public InstructionsRepository getInstructionsRepository() {
        if (instructionsRepository == null) {
            instructionsRepository =
                new InstructionsService(getConfigurationModule().getPaymentSettings(),
                    networkModule.getRetrofitClient().create(InstructionsClient.class));
        }
        return instructionsRepository;
    }

    public IssuersRepository getIssuersRepository() {
        if (issuersRepository == null) {
            final com.mercadopago.android.px.internal.services.IssuersService issuersService =
                networkModule.getRetrofitClient().create(
                    com.mercadopago.android.px.internal.services.IssuersService.class);

            issuersRepository = new IssuersServiceImp(issuersService, getConfigurationModule().getPaymentSettings(),
                getConfigurationModule().getUserSelectionRepository());
        }
        return issuersRepository;
    }

    public CardTokenRepository getCardTokenRepository() {
        if (cardTokenRepository == null) {
            final GatewayService gatewayService =
                networkModule.getRetrofitClient().create(GatewayService.class);
            cardTokenRepository = new CardTokenService(gatewayService, getConfigurationModule().getPaymentSettings(),
                new Device(getApplicationContext()), getMercadoPagoESC());
        }
        return cardTokenRepository;
    }

    public BankDealsRepository getBankDealsRepository() {
        if (bankDealsRepository == null) {
            final BankDealService bankDealsService =
                networkModule.getRetrofitClient().create(BankDealService.class);
            bankDealsRepository = new BankDealsService(bankDealsService, getApplicationContext(),
                getConfigurationModule().getPaymentSettings());
        }
        return bankDealsRepository;
    }

    public IdentificationRepository getIdentificationRepository() {
        if (identificationRepository == null) {
            final com.mercadopago.android.px.internal.services.IdentificationService identificationService =
                networkModule.getRetrofitClient()
                    .create(com.mercadopago.android.px.internal.services.IdentificationService.class);
            identificationRepository =
                new IdentificationService(identificationService, getConfigurationModule().getPaymentSettings());
        }
        return identificationRepository;
    }

    public PaymentMethodsRepository getPaymentMethodsRepository() {
        if (paymentMethodsRepository == null) {
            final CheckoutService checkoutService =
                networkModule.getRetrofitClient().create(CheckoutService.class);
            paymentMethodsRepository =
                new PaymentMethodsService(getConfigurationModule().getPaymentSettings(), checkoutService);
        }
        return paymentMethodsRepository;
    }

    public CongratsRepository getCongratsRepository() {
        if (congratsRepository == null) {
            final Context applicationContext = getApplicationContext();
            final CongratsService congratsService =
                networkModule.getRetrofitClient().create(CongratsService.class);
            final PaymentSettingRepository paymentSettings = getConfigurationModule().getPaymentSettings();
            congratsRepository =
                new CongratsRepositoryImpl(congratsService, getInitRepository(), paymentSettings,
                    getPlatform(applicationContext),
                    networkModule.getFlowIdProvider(), configurationModule.getUserSelectionRepository(),
                    amountRepository,
                    configurationModule.getDisabledPaymentMethodRepository(),
                    configurationModule.getPayerComplianceRepository(),
                    getMercadoPagoESC());
        }
        return congratsRepository;
    }

    public ViewModelModule getViewModelModule() {
        if (viewModelModule == null) {
            viewModelModule = new ViewModelModule();
        }
        return viewModelModule;
    }

    public NetworkModule getNetworkModule() {
        return networkModule;
    }

    public PrefetchInitService getPrefetchInitService(@NonNull final MercadoPagoCheckout checkout) {
        return new PrefetchInitService(checkout, networkModule.getRetrofitClient().create(CheckoutService.class),
            getMercadoPagoESC(), networkModule.getFlowIdProvider());
    }

    public void initIds(@NonNull final MercadoPagoCheckout checkout) {
        //Favoring product id in discount params because that one is surely custom if exists
        final String deprecatedProductId =
            checkout.getAdvancedConfiguration().getDiscountParamsConfiguration().getProductId();
        final String productId = TextUtil.isNotEmpty(deprecatedProductId) ? deprecatedProductId
            : checkout.getAdvancedConfiguration().getProductId();
        networkModule.getSessionIdProvider().configure(checkout.getTrackingConfiguration().getSessionId());
        MPTracker.getInstance().setSessionId(checkout.getTrackingConfiguration().getSessionId());
        final boolean securityEnabled = BehaviourProvider.getSecurityBehaviour()
            .isSecurityEnabled(new SecurityValidationData.Builder(productId).build());
        MPTracker.getInstance().setSecurityEnabled(securityEnabled);
        networkModule.getProductIdProvider().configure(productId);
    }
}